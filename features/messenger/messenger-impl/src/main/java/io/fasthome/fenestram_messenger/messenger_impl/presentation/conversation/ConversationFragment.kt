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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class ConversationFragment :
    BaseFragment<ConversationState, ConversationEvent>(R.layout.fragment_conversation) {

    override val vm: ConversationViewModel by viewModel()

    val formater = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

    private val binding by fragmentViewBinding(FragmentConversationBinding::bind)

    private val conversationAdapter = ConversationAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        binding.messagesList.adapter = conversationAdapter

        binding.sendButton.setOnClickListener() {

            val currentTime = LocalTime.now()
            val formated = currentTime.format(formater)

            when {
                !binding.inputMessage.text.toString()
                    .isNullOrBlank() -> vm.addMessageToConversation(
                    binding.inputMessage.text.toString(),
                    formated
                )
            }
            binding.inputMessage.text.clear()
            binding.messagesList.scrollToPosition(conversationAdapter.itemCount)
        }
        binding.backButton.setOnClickListener() {
            vm.exitToMessenger()
        }
    }

    override fun renderState(state: ConversationState) {
        when {
            state.messages.isNotEmpty() -> conversationAdapter.items = state.messages
        }
    }

    override fun handleEvent(event: ConversationEvent) = noEventsExpected()
}