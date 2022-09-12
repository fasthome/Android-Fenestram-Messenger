package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentConversationBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.ConversationAdapter
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.dp
import io.fasthome.fenestram_messenger.util.increaseHitArea
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText


class ConversationFragment : BaseFragment<ConversationState, ConversationEvent>(R.layout.fragment_conversation) {

    override val vm: ConversationViewModel by viewModel(getParamsInterface = ConversationNavigationContract.getParams)

    private val binding by fragmentViewBinding(FragmentConversationBinding::bind)

    private val conversationAdapter = ConversationAdapter(onGroupProfileItemClicked = {
        vm.onGroupProfileClicked(it)
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        binding.messagesList.adapter = conversationAdapter

        binding.sendButton.setOnClickListener() {
            when {
                !binding.inputMessage.text.toString()
                    .isNullOrBlank() -> vm.addMessageToConversation(binding.inputMessage.text.toString())
            }
            binding.inputMessage.text?.clear()
            binding.messagesList.scrollToPosition(conversationAdapter.itemCount)
        }
        binding.backButton.setOnClickListener() {
            vm.exitToMessenger()
        }
        binding.profileToolBar.onClick {
            vm.onUserClicked()
        }
        binding.backButton.increaseHitArea(16.dp)
    }

    override fun onResume() {
        super.onResume()
        vm.fetchMessages()
    }

    override fun renderState(state: ConversationState) = with(binding) {
        avatarImage.loadCircle(state.avatar)
        if (state.isChatEmpty && emptyContainer.alpha == 0f) {
            emptyContainer.isVisible = true
            emptyContainer
                .animate()
                .alpha(1f)
                .duration = 500
        } else {
            emptyContainer.isVisible = false
            emptyContainer.alpha = 0f
        }
        conversationAdapter.items = state.messages
        if (state.messages.isNotEmpty()) {
            messagesList.smoothScrollToPosition(state.messages.size - 1)
        }
        username.setPrintableText(state.userName)
    }

    override fun handleEvent(event: ConversationEvent) = noEventsExpected()

    override fun onStop() {
        super.onStop()
        vm.closeSocket()
    }
}