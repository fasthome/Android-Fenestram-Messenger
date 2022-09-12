/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentMessengerBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.adapter.MessengerAdapter
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class MessengerFragment :
    BaseFragment<MessengerState, MessengerEvent>(R.layout.fragment_messenger) {

    override val vm: MessengerViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentMessengerBinding::bind)

    private var messageAdapter = MessengerAdapter(
        onChatClicked = {
            vm.launchConversation(it)
        },
        onProfileClicked = {
            vm.onProfileClicked(it)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chatList.adapter = messageAdapter
        vm.fetchChats()
    }

    override fun onResume() {
        super.onResume()
        vm.fetchChats()
        vm.fetchNewMessages()
    }

    override fun renderState(state: MessengerState) {
        messageAdapter.items = state.messengerViewItems
        if (state.messengerViewItems.isNotEmpty()) {
            binding.chatList.smoothScrollToPosition(0)
        }
    }

    override fun handleEvent(event: MessengerEvent) = noEventsExpected()

    override fun onFabClicked(): Boolean {
        vm.onCreateChatClicked()
        return super.onFabClicked()
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.unsubscribeMessages()
    }

}