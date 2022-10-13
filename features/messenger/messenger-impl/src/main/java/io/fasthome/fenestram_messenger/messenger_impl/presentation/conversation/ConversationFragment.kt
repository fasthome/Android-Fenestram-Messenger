package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.DimenRes
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fasthome.component.person_detail.PersonDetailDialog
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.component.select_from.SelectFromDialog
import io.fasthome.fenestram_messenger.core.ui.dialog.AcceptDialog
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.DeleteChatMenuBinding
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentConversationBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.AttachedAdapter
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.ConversationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.dialog.ErrorSentDialog
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.dialog.MessageActionDialog
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.addHeaders
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.singleSameTime
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.SpacingItemDecoration
import io.fasthome.fenestram_messenger.util.*
import java.time.ZonedDateTime


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
    }, onSelfMessageLongClicked = {
        vm.onSelfMessageLongClicked(it)
    }, onReceiveMessageLongClicked = {
        vm.onReceiveMessageLongClicked(it)
    }, onGroupMessageLongClicked = {
        vm.onGroupMessageLongClicked(it)
    }, onSelfImageLongClicked = {
        vm.onSelfImageLongClicked(it)
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

        sendButton.setOnClickListener() {
            vm.addMessageToConversation(inputMessage.text.toString())
            inputMessage.text?.clear()
            messagesList.scrollToPosition(conversationAdapter.itemCount)
        }

        backButton.setOnClickListener() {
            vm.exitToMessenger()
        }
        profileToolBar.onClick {
            vm.onUserClicked(false)
        }
        backButton.increaseHitArea(16.dp)

        dropdownMenu.setOnClickListener {
            vm.onOpenMenu()
        }

        ivCloseEdit.setOnClickListener {
            vm.editMessageMode(false)
            inputMessage.setText("")
        }

        attachButton.onClick {
            vm.onAttachClicked()
        }
        backButton.increaseHitArea(16.dp)

    }

    override fun onResume() {
        super.onResume()
        vm.fetchMessages(isResumed = true)
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
        renderStateEditMode(state.editMode, state.messageToEdit)
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

            ConversationEvent.MessageSent -> binding.messagesList.post {
                binding.messagesList.scrollToPosition(0)
            }
            ConversationEvent.InvalidateList -> conversationAdapter.notifyDataSetChanged()
            ConversationEvent.ShowSelectFromDialog ->
                SelectFromDialog
                    .create(
                        fragment = this,
                        fromCameraClicked = {
                            vm.selectFromCamera()
                        },
                        fromGalleryClicked = {
                            vm.selectFromGallery()
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
            is ConversationEvent.ShowSelfMessageActionDialog -> {
                val canEdit =
                    event.conversationViewItem.date?.plusDays(1)?.isAfter(ZonedDateTime.now())
                        ?: false
                MessageActionDialog.create(
                    fragment = this,
                    onDelete = {
                        vm.onDeleteMessageClicked(event.conversationViewItem)
                    }, onCopy = {
                        copyPrintableText(event.conversationViewItem.content)
                    }, onEdit = if (canEdit) {
                        { vm.editMessageMode(true, event.conversationViewItem) }
                    } else null
                ).show()
            }
            is ConversationEvent.ShowReceiveMessageActionDialog -> MessageActionDialog.create(
                fragment = this,
                onCopy = {
                    copyPrintableText(event.conversationViewItem.content)
                }

            ).show()
            is ConversationEvent.ShowGroupMessageActionDialog -> MessageActionDialog.create(
                fragment = this,
                onCopy = {
                    copyPrintableText(event.conversationViewItem.content)
                }

            ).show()
            is ConversationEvent.ShowSelfImageActionDialog -> MessageActionDialog.create(
                fragment = this,
                onDelete = {
                    vm.onDeleteMessageClicked(event.conversationViewItem)
                }
            ).show()
        }
    }

    private fun renderStateEditMode(
        isEditMode: Boolean,
        selfMessage: ConversationViewItem.Self.Text? = null,
    ) {
        with(binding) {
            clEditMessage.isInvisible = !isEditMode
            attachButton.isVisible = !isEditMode
            horizontalPaddingInput(if (isEditMode) R.dimen.input_message_edit_mode_padding else R.dimen.input_message_default_padding)
            val constraintsSet = ConstraintSet().apply {
                clone(root)
                connect(R.id.messages_list,
                    ConstraintSet.BOTTOM,
                    if (isEditMode) R.id.cl_edit_message else R.id.input_message,
                    ConstraintSet.TOP)
            }
            root.setConstraintSet(constraintsSet)
            if (!isEditMode || selfMessage == null) return
            inputMessage.setOnSizeChanged(onHeightChanged = {
                clEditMessage.setPadding(0, 0, 0, it + 10.dp)
            })
            val textToEdit = getPrintableText(selfMessage.content)
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
            binding.inputMessage.paddingTop)
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