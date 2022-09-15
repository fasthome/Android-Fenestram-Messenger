package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import io.fasthome.fenestram_messenger.core.ui.dialog.DeleteChatDialog
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentConversationBinding
import io.fasthome.fenestram_messenger.messenger_impl.databinding.MenuBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.ConversationAdapter
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.PopupMenu


class ConversationFragment :
    BaseFragment<ConversationState, ConversationEvent>(R.layout.fragment_conversation) {

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

        binding.dropdownMenu.setOnClickListener {
            vm.onOpenMenu()
        }


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

    override fun handleEvent(event: ConversationEvent) {
        when (event) {
            is ConversationEvent.OpenMenuEvent -> {
                val menuBinding = MenuBinding.inflate(requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                val popupMenu = PopupMenu.create(menuBinding.conversationMenu)
                popupMenu.showAsDropDown(binding.dropdownMenu)

                menuBinding.delete.onClick {
                    vm.showDialog()
                    popupMenu.dismiss()
                }

                menuBinding.edit.onClick {
                    //TODO Редактирование
                    popupMenu.dismiss()
                }

            }
            is ConversationEvent.ShowDialog -> DeleteChatDialog.create(
                this,
                PrintableText.StringResource(R.string.common_delete_chat_dialog),
                vm::deleteChat,
                event.id
            )
                .show()

        }
    }

    override fun onStop() {
        super.onStop()
        vm.closeSocket()
    }
}