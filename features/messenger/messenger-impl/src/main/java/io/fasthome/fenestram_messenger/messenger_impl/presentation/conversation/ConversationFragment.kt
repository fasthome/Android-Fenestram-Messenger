package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.size
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentConversationBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.ConversationAdapter
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class ConversationFragment : BaseFragment<ConversationState,ConversationEvent> (R.layout.fragment_conversation) {

    override val vm: ConversationViewModel by viewModel(getParamsInterface = ConversationNavigationContract.getParams)

    private val binding by fragmentViewBinding(FragmentConversationBinding::bind)

    private val conversationAdapter = ConversationAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        binding.messagesList.adapter = conversationAdapter

        binding.sendButton.setOnClickListener(){
            when{
                !binding.inputMessage.text.toString().isNullOrBlank() -> vm.addMessageToConversation(binding.inputMessage.text.toString())
            }
            binding.inputMessage.text.clear()
            binding.messagesList.scrollToPosition(conversationAdapter.itemCount)
        }
        binding.backButton.setOnClickListener(){
            vm.exitToMessenger()
        }
        binding.profileToolBar.onClick {
            vm.onUserClicked()
        }
    }

    override fun onResume() {
        super.onResume()
        vm.fetchMessages()
    }

    override fun renderState(state: ConversationState) {
        when{
            state.messages.isNotEmpty() -> {
                conversationAdapter.items = state.messages
                binding.messagesList.smoothScrollToPosition(state.messages.size - 1)
            }
        }
        binding.username.setPrintableText(state.userName)
    }

    override fun handleEvent(event: ConversationEvent) = noEventsExpected()

    override fun onStop() {
        super.onStop()
        vm.closeSocket()
    }
}