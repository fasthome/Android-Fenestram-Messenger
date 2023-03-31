package io.fasthome.fenestram_messenger.presentation.base.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import com.dolatkia.animatedThemeManager.ThemeManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.fasthome.fenestram_messenger.navigation.BackPressConsumer
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.ModalWindow
import io.fasthome.fenestram_messenger.presentation.base.R
import io.fasthome.fenestram_messenger.presentation.base.util.onBackPressed
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.awaitPost
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.android.ext.android.inject
import kotlin.coroutines.resume
import kotlin.reflect.KClass


open class BaseBottomSheetFragment(
    @LayoutRes bottomSheetRes: Int,
    private val contentClazz: KClass<out BaseFragment<*, *>>,
    private val config: Config = Config(),
) : Fragment(bottomSheetRes), ModalWindow, BackPressConsumer {

    private companion object {

        const val CONTENT_TAG = "CONTENT_FRAGMENT"

        const val KEY_SAVED_STATE = "KEY_SAVED_STATE"
        const val KEY_BEHAVIOR_STATE = "KEY_BEHAVIOR_STATE"
        const val KEY_DISMISSED = "KEY_DISMISSED"
    }

    data class Config(
        val scale: Scale = Scale.Fullscreen,
        val canceledOnTouchOutside: Boolean = true,
        val fitSystemWindow: Boolean = true
    ) {
        sealed class Scale {
            object Fullscreen : Scale()
            object FitToContent : Scale()
            data class Percent(val collapsed: Float) : Scale()
            data class Absolute(val collapsed: Int) : Scale()
        }
    }

    val router: ContractRouter by inject()
    var fragmentInstance: BaseFragment<*, *>? = null

    var currentState: BottomSheetState? = null
    var dismissed: Boolean = false

    private var _behavior: BottomSheetBehavior<FragmentContainerView>? = null
    protected val behavior get() = checkNotNull(_behavior)

    var beforeDismissListener: (() -> Unit)? = null

    private var stateCallback = StateChangeListener { newState ->
        currentState = newState
        if (newState == BottomSheetState.HIDDEN) {
            if (fragmentInstance is BottomSheetDismissListener) {
                (fragmentInstance as BottomSheetDismissListener).onDismiss()
            } else {
                router.exit()
            }
        }
    }

    fun setStateCallback(listener: StateChangeListener) {
        stateCallback = listener
        /* _behavior?.addBottomSheetCallback(stateCallback)
        _behavior?.addBottomSheetCallback(slideCallback)*/
    }

    private val slideCallback = SlideChangeListener { slideOffset ->
        handleSlideCallback(slideOffset)
        fragmentInstance?.handleSlideCallback(slideOffset)
    }

    fun getThemeManager(): ThemeManager {
        return ThemeManager.instance
    }

    fun getTheme(): Theme {
        return getThemeManager().getCurrentTheme() as Theme
    }

    open fun syncTheme(appTheme: Theme) {}

    override fun onResume() {
        getThemeManager().getCurrentLiveTheme().observe(this) {
            syncTheme(it as Theme)
        }

        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedStateRegistry.run {
            registerSavedStateProvider(KEY_SAVED_STATE) {
                bundleOf(
                    KEY_BEHAVIOR_STATE to currentState,
                    KEY_DISMISSED to dismissed
                )
            }
            consumeRestoredStateForKey(KEY_SAVED_STATE)?.let { saved ->
                if (saved.getBoolean(KEY_DISMISSED)) {
                    currentState = BottomSheetState.HIDDEN
                    if (fragmentInstance is BottomSheetDismissListener) {
                        (fragmentInstance as BottomSheetDismissListener).onDismiss()
                    } else {
                        router.exit()
                    }
                } else {
                    currentState = saved.getSerializable(KEY_BEHAVIOR_STATE) as? BottomSheetState
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _behavior = CustomBottomSheetBehavior<FragmentContainerView>(config.scale).apply {
            currentState
                ?.let { setState(it) }
                ?: let {
                    currentState = BottomSheetState.COLLAPSED
                    setState(BottomSheetState.HIDDEN)
                }
            addBottomSheetCallback(stateCallback)
            addBottomSheetCallback(slideCallback)
        }

        if (childFragmentManager.findFragmentByTag(CONTENT_TAG) == null) {
            fragmentInstance = contentClazz.java.newInstance()
            fragmentInstance?.arguments = this@BaseBottomSheetFragment.arguments
            childFragmentManager.commitNow {
                replace(R.id.content, fragmentInstance ?: return@commitNow, CONTENT_TAG)
            }
            this.view?.findViewById<ViewGroup>(R.id.cl_root)?.fitsSystemWindows = config.fitSystemWindow
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            view.awaitPost()
            currentState?.let { behavior.setState(it) }
        }
    }

    override fun onDestroyView() {
        behavior.apply {
            removeBottomSheetCallback(stateCallback)
            removeBottomSheetCallback(slideCallback)
        }
        _behavior = null

        super.onDestroyView()
    }

    override suspend fun dismiss() {
        when {
            dismissed -> throw CancellationException()
            currentState == BottomSheetState.HIDDEN -> return
            else -> {
                dismissed = true
                viewLifecycleOwner.lifecycleScope
                    .launchWhenCreated { performDismiss() }
                    .join()
            }
        }
    }

    fun onViewCreated(rootView: View, contentView: FragmentContainerView) {
        rootView.apply {
            if (config.canceledOnTouchOutside) setOnClickListener {
                if (beforeDismissListener == null) router.exit()
                else beforeDismissListener?.invoke()
            }
        }
        contentView.apply {
            layoutParams = if (config.scale == Config.Scale.FitToContent) {
                CoordinatorLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            } else {
                CoordinatorLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            setBehavior(behavior)
        }

    }

    private suspend fun performDismiss() = suspendCancellableCoroutine<Unit> { continuation ->
        val listener = StateChangeListener { newState ->
            if (newState == BottomSheetState.HIDDEN) {
                continuation.resume(Unit)
            }
        }
        behavior.apply {
            removeBottomSheetCallback(stateCallback)
            addBottomSheetCallback(listener)
            isHideable = true
            isDraggable = false
            setState(BottomSheetState.HIDDEN)
        }
        continuation.invokeOnCancellation { behavior.removeBottomSheetCallback(listener) }
    }

    override fun onBackPressed(): Boolean {
        if (beforeDismissListener != null) {
            beforeDismissListener!!.invoke()
            return true
        }
        return childFragmentManager.onBackPressed()
    }

    enum class BottomSheetState(val flag: Int, val alpha: Int) {
        HIDDEN(BottomSheetBehavior.STATE_HIDDEN, 0),
        COLLAPSED(BottomSheetBehavior.STATE_COLLAPSED, 255),
        EXPANDED(BottomSheetBehavior.STATE_EXPANDED, 255)
    }

    private class CustomBottomSheetBehavior<V : View>(
        private val scale: Config.Scale,
    ) : BottomSheetBehavior<V>() {

        init {
            isHideable = true
            isFitToContents = true
            isGestureInsetBottomIgnored = true
        }

        override fun onLayoutChild(parent: CoordinatorLayout, child: V, direction: Int): Boolean =
            super.onLayoutChild(parent, child, direction).also {
                peekHeight = calculatePeekHeight(child.height)
            }

        private fun calculatePeekHeight(height: Int): Int =
            when (scale) {
                Config.Scale.Fullscreen -> height
                Config.Scale.FitToContent -> height
                is Config.Scale.Absolute -> scale.collapsed
                is Config.Scale.Percent -> (height * scale.collapsed).toInt()
            }

    }

    fun BottomSheetBehavior<*>.setState(state: BottomSheetState) {
        this.state = state.flag
    }

    private fun <V : View> V.setBehavior(behavior: BottomSheetBehavior<V>?) {
        (layoutParams as CoordinatorLayout.LayoutParams).behavior = behavior
    }

    class StateChangeListener(
        val block: (newState: BottomSheetState) -> Unit,
    ) : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            BottomSheetState.values().find { it.flag == newState }?.let(block)
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
    }

    private class SlideChangeListener(
        val block: (slideOffset: Float) -> Unit,
    ) : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) = Unit
        override fun onSlide(bottomSheet: View, slideOffset: Float) = block(slideOffset)
    }

    open fun handleSlideCallback(slideOffset: Float) {}
}

interface BottomSheetDismissListener {
    fun onDismiss()
}