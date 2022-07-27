package io.fasthome.fenestram_messenger.debug_impl.presentation.socket

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.debug_impl.R
import io.fasthome.fenestram_messenger.debug_impl.databinding.FragmnetSocketBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.onClick

class SocketFragment : BaseFragment<SocketState, SocketEvent>(R.layout.fragmnet_socket) {

    private val binding by fragmentViewBinding(FragmnetSocketBinding::bind)

    override val vm: SocketViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        socketSendMessage.onClick {
            vm.onSendMessageClicked()
        }
        socketBack.onClick {
            vm.onBackClicked()
        }
    }

    override fun renderState(state: SocketState) = nothingToRender()
    override fun handleEvent(event: SocketEvent) = noEventsExpected()

}