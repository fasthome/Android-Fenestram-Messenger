/**
 * Created by Dmitry Popov on 10.01.2023.
 */
package io.fasthome.fenestram_messenger.call_impl.presentation.create_call

import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class CreateCallFragment : BaseFragment<CreateCallState, CreateCallEvent>() {

    override val vm: CreateCallViewModel by viewModel()

    override fun renderState(state: CreateCallState) = nothingToRender()

    override fun handleEvent(event: CreateCallEvent) = noEventsExpected()


}