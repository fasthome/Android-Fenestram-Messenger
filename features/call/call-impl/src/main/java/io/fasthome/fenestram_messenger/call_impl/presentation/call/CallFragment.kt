/**
 * Created by Dmitry Popov on 10.01.2023.
 */
package io.fasthome.fenestram_messenger.call_impl.presentation.call

import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class CallFragment : BaseFragment<CallState, CallEvent>() {

    override val vm: CallViewModel by viewModel()

    override fun renderState(state: CallState) = nothingToRender()

    override fun handleEvent(event: CallEvent) = noEventsExpected()


}