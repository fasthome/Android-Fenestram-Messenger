/**
 * Created by Dmitry Popov on 22.05.2022.
 */
package io.fasthome.fenestram_messenger.presentation.base.ui

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.mvi.BaseViewEvent
import io.fasthome.fenestram_messenger.mvi.ViewModelInterface
import io.fasthome.fenestram_messenger.navigation.BackPressConsumer
import io.fasthome.fenestram_messenger.presentation.base.util.onBackPressed
import io.fasthome.fenestram_messenger.presentation.base.util.showMessage
import io.fasthome.fenestram_messenger.util.collectWhenStarted
import io.fasthome.fenestram_messenger.util.doOnCreate
import io.fasthome.fenestram_messenger.util.doOnStartStop

abstract class BaseFragment<State : Any, Event : Any> : Fragment, BackPressConsumer {

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
            }
        }

        lifecycle.doOnStartStop(onStart = vm::onViewActive, onStop = vm::onViewInactive)
        //TODO придумать более подходящее решение
        lifecycle.doOnCreate(vm::onCreate)

//        childFragmentManager.setFragmentResultListener(
//            requestParams.requestKey,
//            this
//        ) { _, bundle ->
//            vm.onMessageResult(bundle.messageResult)
//        }
    }

    override fun onBackPressed(): Boolean =
        childFragmentManager.onBackPressed() || vm.onBackPressed()

    protected abstract fun renderState(state: State)
    protected abstract fun handleEvent(event: Event)
}