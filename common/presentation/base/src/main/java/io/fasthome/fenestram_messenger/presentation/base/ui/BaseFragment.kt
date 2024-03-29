/**
 * Created by Dmitry Popov on 22.05.2022.
 */
package io.fasthome.fenestram_messenger.presentation.base.ui

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.dolatkia.animatedThemeManager.ThemeManager
import io.fasthome.fenestram_messenger.mvi.BaseViewEvent
import io.fasthome.fenestram_messenger.mvi.ViewModelInterface
import io.fasthome.fenestram_messenger.mvi.messageResult
import io.fasthome.fenestram_messenger.navigation.BackPressConsumer
import io.fasthome.fenestram_messenger.navigation.FabConsumer
import io.fasthome.fenestram_messenger.navigation.model.requestParams
import io.fasthome.fenestram_messenger.presentation.base.util.onBackPressed
import io.fasthome.fenestram_messenger.presentation.base.util.onFabClicked
import io.fasthome.fenestram_messenger.presentation.base.util.onFabUpdateIcon
import io.fasthome.fenestram_messenger.presentation.base.util.showMessage
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.collectWhenStarted
import io.fasthome.fenestram_messenger.util.doOnCreate
import io.fasthome.fenestram_messenger.util.doOnStartStop

abstract class BaseFragment<State : Any, Event : Any> : Fragment, BackPressConsumer, FabConsumer {

    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    protected abstract val vm: ViewModelInterface<State, BaseViewEvent<Event>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.viewState.collectWhenStarted(this, ::renderState)
        vm.viewEvent.collectWhenStarted(this) { event ->
            when (event) {
                is BaseViewEvent.ScreenEvent -> handleEvent(event.event)
                is BaseViewEvent.ShowMessage -> showMessage(event.message)
                is BaseViewEvent.ShowDialog -> showMessage(event.message, event.onCloseClick, event.onRetryClick)
            }
        }

        lifecycle.doOnStartStop(onStart = vm::onViewActive, onStop = vm::onViewInactive)
        //TODO придумать более подходящее решение
        lifecycle.doOnCreate(vm::onCreate)

        childFragmentManager.setFragmentResultListener(
            requestParams.requestKey,
            this
        ) { _, bundle ->
            vm.onMessageResult(bundle.messageResult)
        }
    }

    override fun onResume() {
        getThemeManager().getCurrentLiveTheme().observe(this) {
            syncTheme(it as Theme)
        }

        super.onResume()
    }

    fun getThemeManager() : ThemeManager {
        return ThemeManager.instance
    }

    fun getTheme(): Theme {
        return getThemeManager().getCurrentTheme() as Theme
    }

    // to sync ui with selected theme
    open fun syncTheme(appTheme: Theme){}

    override fun onBackPressed(): Boolean =
        childFragmentManager.onBackPressed() || vm.onBackPressed()

    override fun onFabClicked(): Boolean =
        childFragmentManager.onFabClicked()

    override fun updateFabIcon(iconRes: Int?, badgeCount: Int) {
        requireActivity().supportFragmentManager.onFabUpdateIcon(iconRes, badgeCount)
    }

    protected abstract fun renderState(state: State)
    protected abstract fun handleEvent(event: Event)

    open fun handleSlideCallback(offset : Float){}
}