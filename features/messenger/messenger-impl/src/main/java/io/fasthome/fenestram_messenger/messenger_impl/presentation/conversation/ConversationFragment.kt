package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.annotation.DimenRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.component.person_detail.PersonDetailDialog
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.component.select_from.SelectFromConversation
import io.fasthome.fenestram_messenger.core.ui.dialog.AcceptDialog
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.DeleteChatMenuBinding
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentConversationBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.AttachedAdapter
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.ConversationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.dialog.ErrorSentDialog
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.dialog.MessageActionDialog
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.addHeaders
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.singleSameTime
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationImageItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationTextItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.SpacingItemDecoration
import io.fasthome.fenestram_messenger.uikit.custom_view.ViewBinderHelper
import io.fasthome.fenestram_messenger.util.*
import java.time.ZonedDateTime


class ConversationFragment :
    BaseFragment<ConversationState, ConversationEvent>(R.layout.fragment_conversation) {

    private val binding by fragmentViewBinding(FragmentConversationBinding::bind)

    private val pickFileFragment by registerFragment(
        componentFragmentContractInterface = PickFileComponentContract,
        paramsProvider = {
            PickFileComponentParams(
                mimeType = PickFileComponentParams.MimeType.Image(compressToSize = null)
            )
        }
    )

    private val permissionFragment by registerFragment(
        componentFragmentContractInterface = PermissionComponentContract
    )

    override val vm: ConversationViewModel by viewModel(
        getParamsInterface = ConversationNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::pickFileFragment)
            .register(::permissionFragment)
    )

    private val conversationAdapter = ConversationAdapter(
        viewBinderHelper = ViewBinderHelper(),
        onGroupProfileItemClicked = {
            vm.onGroupProfileClicked(it)
        }, onImageClicked = {
            vm.onImageClicked(it)
        }, onSelfDownloadDocument = { item, progressListener ->
            vm.onDownloadDocument(itemSelf = item, progressListener = progressListener)
        }, onRecieveDownloadDocument = { item, progressListener ->
            vm.onDownloadDocument(itemReceive = item, progressListener = progressListener)
        }, onGroupDownloadDocument = { item, progressListener ->
            vm.onDownloadDocument(itemGroup = item, progressListener = progressListener)
        }, onSelfMessageLongClicked = {
            vm.onSelfMessageLongClicked(it)
        }, onReceiveMessageLongClicked = {
            vm.onReceiveMessageLongClicked(it)
        }, onGroupMessageLongClicked = {
            vm.onGroupMessageLongClicked(it)
        }, onSelfImageLongClicked = {
            vm.onSelfMessageLongClicked(it)
        }, onSelfTextReplyImageLongClicked = {
            vm.onSelfMessageLongClicked(it)
        }, onReceiveTextReplyImageLongClicked = {
            vm.onReceiveTextReplyImageLongClicked(it)
        }, onReplyMessageText = {
            vm.replyMessageMode(isReplyMode = true, conversationViewItem = it)
        }, onGroupImageLongClicked = {
            vm.onGroupMessageLongClicked(it)
        }, onReceiveImageLongClicked = {
            vm.onReceiveMessageLongClicked(it)
        }, onReplyMessageImage = {
            vm.replyMessageMode(isReplyMode = true, conversationViewItem = it)
        }, onGroupTextReplyImageLongClicked = {
            vm.onGroupMessageLongClicked(it)
        }, onSelfForwardLongClicked = {
            vm.onSelfForwardLongClicked(it)
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
                if (vm.firstVisibleItemPosition != linearLayoutManager.findFirstVisibleItemPosition()) {
                    vm.firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                    vm.onScrolledToLastPendingMessage()
                }
                if (lastScrollPosition == conversationAdapter.itemCount - 1) {
                    vm.loadItems(isResumed)
                }
            }
        })
        messagesList.itemAnimator = null
        messagesList.addItemDecoration(SpacingItemDecoration { index, itemCount ->
            val bottomMargin = if (conversationAdapter.items[index].timeVisible) {
                16.dp
            } else {
                8.dp
            }

            Rect(
                0.dp,
                0.dp,
                0.dp,
                bottomMargin,
            )
        })
        attachedList.adapter = attachedAdapter

        sendButton.onClick() {
            vm.addMessageToConversation(inputMessage.text.toString())
            inputMessage.text?.clear()
            messagesList.scrollToPosition(conversationAdapter.itemCount)
        }

        backButton.onClick() {
            vm.exitToMessenger()
        }
        profileToolBar.onClick {
            vm.onUserClicked(false)
        }
        backButton.increaseHitArea(16.dp)

        dropdownMenu.onClick {
            vm.onOpenMenu()
        }
        ivCloseEdit.increaseHitArea(16.dp)
        ivCloseEdit.onClick {
            vm.editMessageMode(false)
            inputMessage.setText("")
        }

        attachButton.onClick {
            vm.onAttachClicked()
        }

        inputMessage.addTextChangedListener { vm.onTypingMessage() }

        pendingMessagesButton.onClick {
            messagesList.smoothScrollToPosition(0)
        }
    }

    override fun onResume() {
        super.onResume()
        vm.fetchMessages(isResumed = true)
    }

    override fun renderState(state: ConversationState) = with(binding) {
        avatarImage.loadCircle(url = state.avatar, placeholderRes = R.drawable.ic_avatar_placeholder)
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
        val newItems = state
            .messages
            .toList()
            .map { it.second }
            .addHeaders()
            .singleSameTime()
        conversationAdapter.items = newItems
        username.setPrintableText(state.userName)
        attachedList.isVisible = state.attachedFiles.isNotEmpty()
        attachedAdapter.items = state.attachedFiles
        userStatusView.setPrintableText(state.userStatus)
        userStatusDots.setPrintableText(state.userStatusDots)
        if (state.newMessagesCount == 0) {
            pendingMessagesButton.isVisible = false
            pendingAmount.isVisible = false
            pendingMessagesStatus.isVisible = false
            pendingMessagesArrow.isVisible = false
        } else {
            pendingMessagesButton.isVisible = true
            pendingAmount.isVisible = true
            pendingMessagesStatus.isVisible = true
            pendingMessagesArrow.isVisible = true
            pendingAmount.text = state.newMessagesCount.toString()
        }

        when (state.inputMessageMode) {
            is InputMessageMode.Default -> {
                renderStateEditMode(false, null)
                renderStateReplyMode(false, null)
            }
            is InputMessageMode.Edit -> {
                renderStateReplyMode(false, null)
                renderStateEditMode(true, state.inputMessageMode.messageToEdit)
            }
            is InputMessageMode.Reply -> {
                renderStateEditMode(false, null)
                renderStateReplyMode(true, state.inputMessageMode.messageToReply)
            }
        }

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
                    vm.onUserClicked(true)
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
            is ConversationEvent.ShowDeleteChatDialog -> AcceptDialog.create(
                fragment = this,
                titleText = PrintableText.StringResource(R.string.common_delete_chat_dialog),
                accept = vm::deleteChat,
                id = event.id
            ).show()

            is ConversationEvent.UpdateScrollPosition -> {
                binding.messagesList.post {
                    (binding.messagesList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        event.scrollPosition,
                        binding.messagesList.height - 48.dp
                    )
                }
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
            is ConversationEvent.ShowPersonDetailDialog ->
                if (!PersonDetailDialog.isShowing()) {
                    PersonDetailDialog
                        .create(
                            fragment = this,
                            personDetail = event.selectedPerson,
                            launchFaceCallClicked = {
                                //TODO
                            },
                            launchCallClicked = {
                                //TODO
                            },
                            launchConversationClicked = {
                                vm.onLaunchConversationClicked(it)
                            },
                            onAvatarClicked = { avatarUrl ->
                                vm.onImageClicked(url = avatarUrl)
                            })
                        .show()
                }
            is ConversationEvent.ShowErrorSentDialog -> {
                ErrorSentDialog.create(
                    fragment = this,
                    onRetryClicked = {
                        vm.onRetrySentClicked(event.conversationViewItem)
                    }, onCancelClicked = {
                        vm.onCancelSentClicked(event.conversationViewItem)
                    }
                ).show()
            }

            is ConversationEvent.ShowSelfMessageActionDialog -> when (event.conversationViewItem) {
                is ConversationViewItem.Self.Text -> replyTextDialog(event.conversationViewItem)
                is ConversationViewItem.Self.TextReplyOnImage -> replyTextDialog(event.conversationViewItem)
                is ConversationViewItem.Self.Image -> replyImageDialog(event.conversationViewItem)
                is ConversationViewItem.Self.Document -> Unit
            }

            is ConversationEvent.ShowReceiveMessageActionDialog -> when (event.conversationViewItem) {
                is ConversationViewItem.Receive.Text -> replyTextDialog(event.conversationViewItem)
                is ConversationViewItem.Receive.TextReplyOnImage -> replyTextDialog(event.conversationViewItem)
                is ConversationViewItem.Receive.Image -> replyTextDialog(event.conversationViewItem)
                is ConversationViewItem.Receive.Document -> Unit
            }
            is ConversationEvent.ShowGroupMessageActionDialog -> when (event.conversationViewItem) {
                is ConversationViewItem.Group.Text -> replyTextDialog(event.conversationViewItem)
                is ConversationViewItem.Group.TextReplyOnImage -> replyTextDialog(event.conversationViewItem)
                is ConversationViewItem.Group.Image -> replyImageDialog(event.conversationViewItem)
                is ConversationViewItem.Group.Document -> Unit
            }
        }
    }

    private fun replyImageDialog(conversationViewItem: ConversationViewItem) = MessageActionDialog.create(
        fragment = this,
        onDelete = {
            if (conversationViewItem is ConversationViewItem.Self) {
                vm.onDeleteMessageClicked(conversationViewItem)
            } else null
        },
        onReply = {
            vm.replyMessageMode(true, conversationViewItem)
        }
    ).show()

    private fun replyTextDialog(conversationViewItem: ConversationViewItem) {

        val canEdit =
            conversationViewItem.date?.plusDays(1)?.isAfter(ZonedDateTime.now())
                ?: false

        MessageActionDialog.create(
            fragment = this,
            onDelete = {
                if (conversationViewItem is ConversationViewItem.Self) {
                    vm.onDeleteMessageClicked(conversationViewItem)
                } else null
            },
            onCopy = {
                copyPrintableText(conversationViewItem.content as PrintableText)
            }, onEdit = if (conversationViewItem is ConversationViewItem.Self) {
                if (canEdit) {
                    { vm.editMessageMode(true, conversationViewItem) }
                } else null
            } else null,
            onReply = {
                vm.replyMessageMode(true, conversationViewItem)
            },
            onForward = {
                vm.openChatSelectorForForward(conversationViewItem.id)
            }
        ).show()
    }

    private fun renderStateReplyMode(isReplyMode: Boolean, message: ConversationViewItem? = null) {
        with(binding) {
            switchInputPlate(isReplyMode)
            if (isReplyMode && message != null) {
                when (message) {
                    is ConversationTextItem -> {
                        tvEditMessageTitle.setTextAppearance(R.style.Text_Gray_12sp)
                        tvTextToEdit.setTextAppearance(R.style.Text_White_12sp)
                        tvEditMessageTitle.text = getPrintableRawText(message.userName)
                        tvEditMessageTitle.isVisible = true
                        replyImage.isVisible = false
                        tvTextToEdit.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        tvTextToEdit.text = getPrintableRawText(message.content)
                    }
                    is ConversationImageItem -> {
                        replyImage.isVisible = true
                        tvEditMessageTitle.isVisible = false
                        replyImage.loadRounded(message.content, radius = 8)
                        tvTextToEdit.setTextAppearance(R.style.Text_Blue_14sp)
                        tvTextToEdit.text =
                            getString(R.string.reply_image_from_ph, getPrintableRawText(message.userName))
                    }
                }
            }
        }
    }

    private fun switchInputPlate(state: Boolean) {
        with(binding) {
            if (state) {
                clEditMessage.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.slide_bottom_to_top
                    )
                )
                clEditMessage.isInvisible = false
                inputMessage.showKeyboard()
            } else {
                clEditMessage.isInvisible = true
            }

            attachButton.isVisible = !state
            horizontalPaddingInput(if (state) R.dimen.input_message_edit_mode_padding else R.dimen.input_message_default_padding)
            val constraintsSet = ConstraintSet().apply {
                clone(root)
                connect(
                    R.id.messages_list,
                    ConstraintSet.BOTTOM,
                    if (state) R.id.cl_edit_message else R.id.input_message,
                    ConstraintSet.TOP
                )
            }
            root.setConstraintSet(constraintsSet)
        }
    }


    private fun renderStateEditMode(
        isEditMode: Boolean,
        selfMessage: ConversationViewItem.Self? = null,
    ) {
        with(binding) {
            switchInputPlate(isEditMode)
            if (!isEditMode || selfMessage == null) return
            tvEditMessageTitle.setText(R.string.edit_message_title)
            tvEditMessageTitle.setTextAppearance(R.style.Text_Blue_12sp)
            tvTextToEdit.setTextAppearance(R.style.Text_Gray_12sp)
            inputMessage.setOnSizeChanged(onHeightChanged = {
                clEditMessage.setPadding(0, 0, 0, it + 10.dp)
                clEditMessage.setPadding(0, 0, 0, it + 10.dp)
            })
            val textToEdit = getPrintableText((selfMessage as ConversationTextItem).content)
            tvTextToEdit.text = textToEdit
            inputMessage.setText(textToEdit)
            inputMessage.lastCharFocus()
            inputMessage.showKeyboard()
        }
    }

    private fun horizontalPaddingInput(@DimenRes dimenRes: Int) {
        val padding = resources.getDimension(dimenRes).toInt()
        binding.inputMessage.setPadding(
            padding,
            binding.inputMessage.paddingBottom,
            padding,
            binding.inputMessage.paddingTop
        )
    }

    private fun copyPrintableText(printableText: PrintableText) {
        (requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
            ClipData.newPlainText(
                "copy",
                getPrintableText(printableText)
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.closeSocket()
    }
}