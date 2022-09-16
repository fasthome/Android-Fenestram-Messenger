package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.view.*
import androidx.core.view.isVisible
import io.fasthome.fenestram_messenger.core.ui.dialog.DeleteChatDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.component.select_from.SelectFromDialog
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentConversationBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.AttachedAdapter
import io.fasthome.fenestram_messenger.messenger_impl.databinding.MenuBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.ConversationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.addHeaders
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.PopupMenu
import io.fasthome.fenestram_messenger.util.model.Bytes


class ConversationFragment :
    BaseFragment<ConversationState, ConversationEvent>(R.layout.fragment_conversation) {

    private val binding by fragmentViewBinding(FragmentConversationBinding::bind)

    private val pickImageFragment by registerFragment(
        componentFragmentContractInterface = PickFileComponentContract,
        paramsProvider = {
            PickFileComponentParams(
                mimeType = PickFileComponentParams.MimeType.Image(
                    compressToSize = Bytes(
                        Bytes.BYTES_PER_MB
                    )
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
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
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
        attachedList.isVisible = state.attachedFiles.isNotEmpty()
        attachedAdapter.items = state.attachedFiles
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
        }
    }

    override fun onStop() {
        super.onStop()
        vm.closeSocket()
    }
}