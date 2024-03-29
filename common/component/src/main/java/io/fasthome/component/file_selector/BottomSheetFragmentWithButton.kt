package io.fasthome.component.file_selector

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import io.fasthome.fenestram_messenger.presentation.base.R
import io.fasthome.fenestram_messenger.presentation.base.databinding.FragmentBottomSheetWithButtonBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseBottomSheetFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.view_action.BottomViewAction
import io.fasthome.fenestram_messenger.util.view_action.FileSelectorButtonEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.reflect.KClass

open class BottomSheetFragmentWithButton(
    private val contentClazz: KClass<out BaseFragment<*, *>>,
    private val config: Config = Config(Config.Scale.Fullscreen, true),
) : BaseBottomSheetFragment(
    R.layout.fragment_bottom_sheet_with_button,
    contentClazz,
    config.copy()
) {

    private val binding by fragmentViewBinding(FragmentBottomSheetWithButtonBinding::bind)
    private val bottomViewActions: BottomViewAction<FileSelectorButtonEvent> by inject()
    private var isHaveChanges: Boolean = false


    override fun syncTheme(appTheme: Theme) {
        appTheme.context = requireActivity().applicationContext
        binding.includeBottomInput.clInput.backgroundTintList = ColorStateList.valueOf(appTheme.bg0Color())
    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setStateCallback(StateChangeListener { newState ->
            if (newState == BottomSheetState.HIDDEN) {
                openResetDialog()
                behavior.setState(BottomSheetState.COLLAPSED)
            } else {
                currentState = newState
            }
        })
        super.onViewCreated(view, savedInstanceState)
        onViewCreated(binding.root, binding.content)

        binding.includeBottomInput.btnAttach.onClick {
            bottomViewActions.sendActionToFragment(
                FileSelectorButtonEvent.AttachEvent
            )
        }

        beforeDismissListener = {
            openResetDialog()
        }

        lifecycleScope.launch {
            bottomViewActions.receiveActionsToBottomView().collectLatest { event ->
                CoroutineScope(Dispatchers.Main).launch {
                    when (event) {
                        is FileSelectorButtonEvent.AttachCountEvent -> {
                            isHaveChanges = event.isHaveChanges
                            binding.includeBottomInput.btnAttach.isEnabled = event.count > 0
                            if (event.count > 0) {
                                val text = if (event.oneSelect) {
                                    getString(R.string.common_select_image_selector)
                                } else {
                                    getString(R.string.common_attach) + " " + resources.getQuantityString(
                                        R.plurals.image_quantity,
                                        event.count,
                                        event.count
                                    )
                                }
                                binding.includeBottomInput.btnAttach.text = text
                            } else {
                                binding.includeBottomInput.btnAttach.setText(R.string.common_select_image)
                            }
                        }

                        is FileSelectorButtonEvent.IgnoreRouterExit -> {
                            dismissed = true
                        }
                    }
                }
            }
        }
    }

    private fun openResetDialog() {
        if (!dismissed) {
            if (isHaveChanges) {
                FileSelectorCancelDialog.create(this, getTheme()) {
                    dismissed = true
                    router.exit()
                }.show()
            } else {
                dismissed = true
                router.exit()
            }
        }
    }

    override suspend fun dismiss() {
        openResetDialog()
    }

    override fun handleSlideCallback(slideOffset: Float) {
        if (slideOffset in 0f..1f) return
        binding.includeBottomInput.clInput.translationY =
            1 - slideOffset * binding.includeBottomInput.clInput.height
    }
}

