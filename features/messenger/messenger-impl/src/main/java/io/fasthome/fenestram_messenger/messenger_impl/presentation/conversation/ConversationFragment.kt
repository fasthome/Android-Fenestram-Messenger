package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import io.fasthome.fenestram_messenger.core.ui.dialog.DeleteChatDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentConversationBinding
import io.fasthome.fenestram_messenger.messenger_impl.databinding.MenuBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.ConversationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.addHeaders
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.PopupMenu
import kotlinx.coroutines.flow.distinctUntilChanged


class ConversationFragment :
    BaseFragment<ConversationState, ConversationEvent>(R.layout.fragment_conversation) {

    override val vm: ConversationViewModel by viewModel(getParamsInterface = ConversationNavigationContract.getParams)

    private val binding by fragmentViewBinding(FragmentConversationBinding::bind)

    private val conversationAdapter = ConversationAdapter(onGroupProfileItemClicked = {
        vm.onGroupProfileClicked(it)
    })

    private var lastScrollPosition = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        messagesList.adapter = conversationAdapter
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        messagesList.layoutManager = linearLayoutManager

        messagesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastScrollPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                if(lastScrollPosition == conversationAdapter.itemCount - 1){
                    vm.loadItems()
                }
            }
        })
        messagesList.itemAnimator = null

        sendButton.setOnClickListener() {
            when {
                !inputMessage.text.toString()
                    .isNullOrBlank() -> vm.addMessageToConversation(inputMessage.text.toString())
            }
            inputMessage.text?.clear()
            messagesList.scrollToPosition(conversationAdapter.itemCount)
        }

        backButton.setOnClickListener() {
            vm.exitToMessenger()
        }
        profileToolBar.onClick {
            vm.onUserClicked()
        }
        binding.backButton.increaseHitArea(16.dp)

        binding.dropdownMenu.setOnClickListener {
            vm.onOpenMenu()
        }


    }


    override fun onResume() {
        super.onResume()
        attachButton.onClick {}
        backButton.increaseHitArea(16.dp)

        vm.fetchMessages()

    }

    override fun renderState(state: ConversationState) = with(binding) {
        if (state.avatar.isNotEmpty()) {
            avatarImage.loadCircle(url = state.avatar, placeholderRes = R.drawable.common_avatar)
        }
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
        conversationAdapter.items = state.messages.toList().map { it.second }.addHeaders()
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

            ConversationEvent.MessageSent -> binding.messagesList.post {
                binding.messagesList.scrollToPosition(0)
            }
            ConversationEvent.InvalidateList -> conversationAdapter.notifyDataSetChanged()
        }
    }

    override fun onStop() {
        super.onStop()
        vm.closeSocket()
    }
}