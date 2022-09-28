package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.component.select_from.SelectFromConversation
import io.fasthome.component.select_from.SelectFromDialog
import io.fasthome.fenestram_messenger.core.ui.dialog.DeleteChatDialog
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.DeleteChatMenuBinding
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentConversationBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.AttachedAdapter
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.ConversationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.dialog.ErrorSentDialog
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.addHeaders
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.model.Bytes


class ConversationFragment :
    BaseFragment<ConversationState, ConversationEvent>(R.layout.fragment_conversation) {

    private val binding by fragmentViewBinding(FragmentConversationBinding::bind)

    private val pickImageFragment by registerFragment(
        componentFragmentContractInterface = PickFileComponentContract,
        paramsProvider = {
            PickFileComponentParams(
                mimeType = PickFileComponentParams.MimeType.Image(
                    compressToSize = null
                )
            )
        }
    )

    override val vm: ConversationViewModel by viewModel(
        getParamsInterface = ConversationNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::pickImageFragment)
    )

    private val conversationAdapter = ConversationAdapter(onGroupProfileItemClicked = {
        vm.onGroupProfileClicked(it)
    }, onSelfMessageClicked = {
        vm.onSelfMessageClicked(it)
    }, onImageClicked = {
        vm.onImageClicked(it)
    })

    private val attachedAdapter = AttachedAdapter(
        onRemoveClicked = {
            vm.onAttachedRemoveClicked(it)
        }
    )

    private var lastScrollPosition = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        messagesList.adapter = conversationAdapter
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        messagesList.layoutManager = linearLayoutManager

        messagesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastScrollPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                if (lastScrollPosition == conversationAdapter.itemCount - 1) {
                    vm.loadItems()
                }
            }
        })
        messagesList.itemAnimator = null

        attachedList.adapter = attachedAdapter

        sendButton.setOnClickListener() {
            vm.addMessageToConversation(inputMessage.text.toString())
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

        attachButton.onClick {
            vm.onAttachClicked()
        }
        backButton.increaseHitArea(16.dp)

    }

    override fun onResume() {
        super.onResume()
        vm.fetchMessages()
    }

    override fun renderState(state: ConversationState) = with(binding) {
        avatarImage.loadCircle(url = state.avatar, placeholderRes = R.drawable.common_avatar)
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
        attachedList.isVisible = state.attachedFiles.isNotEmpty()
        attachedAdapter.items = state.attachedFiles
    }

    override fun handleEvent(event: ConversationEvent) {
        when (event) {
            is ConversationEvent.OpenMenuEvent -> {
                val menuBinding = DeleteChatMenuBinding.inflate(layoutInflater)
                val popupMenu = PopupMenu.create(menuBinding.conversationMenu)
                popupMenu.showAsDropDown(binding.dropdownMenu, 0, (-45).dp)

                menuBinding.delete.onClick {
                    vm.showDialog()
                    popupMenu.dismiss()
                }

                menuBinding.edit.onClick {
                    //TODO Редактирование
                    popupMenu.dismiss()
                }

                if (vm.previousScreen())
                    menuBinding.delete.setPrintableText(
                        PrintableText.StringResource(
                            R.string.conversation_delete_from_contacts
                        )
                    )
                else
                    menuBinding.delete.setPrintableText(
                        PrintableText.StringResource(
                            R.string.conversation_delete_from_chat
                        )
                    )
            }
            is ConversationEvent.ShowDeleteChatDialog -> DeleteChatDialog.create(
                fragment = this,
                titleText = PrintableText.StringResource(R.string.common_delete_chat_dialog),
                accept = vm::deleteChat,
                id = event.id
            ).show()

            ConversationEvent.MessageSent -> binding.messagesList.post {
                binding.messagesList.scrollToPosition(0)
            }
            ConversationEvent.InvalidateList -> conversationAdapter.notifyDataSetChanged()
            ConversationEvent.ShowSelectFromDialog ->
                SelectFromConversation
                    .create(
                        fragment = this,
                        fromCameraClicked = {
                            vm.selectFromCamera()
                        },
                        fromGalleryClicked = {
                            vm.selectFromGallery()
                        },
                        attachFileClicked = {
                            vm.selectAttachFile()
                        })
                    .show()
            is ConversationEvent.ShowErrorSentDialog -> {
                ErrorSentDialog.create(
                    fragment = this,
                    onRetryClicked = {
                        vm.onRetrySentClicked(event.conversationViewItem)
                    },
                    onCancelClicked = {
                        vm.onCancelSentClicked(event.conversationViewItem)
                    }
                ).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        vm.closeSocket()
    }
}