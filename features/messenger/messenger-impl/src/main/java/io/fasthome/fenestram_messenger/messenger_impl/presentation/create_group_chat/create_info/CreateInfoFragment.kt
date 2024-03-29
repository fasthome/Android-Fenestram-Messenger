/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.component.select_from.SelectFromDialog
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentCreateInfoChatBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationEvent
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.adapter.ContactsAdapter
import io.fasthome.component.file_selector.FileSelectorNavigationContract
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.RoundedCornersOutlineProvider
import io.fasthome.fenestram_messenger.util.hideKeyboard
import io.fasthome.fenestram_messenger.util.model.Bytes
import io.fasthome.fenestram_messenger.util.onClick

class CreateInfoFragment :
    BaseFragment<CreateInfoState, CreateInfoEvent>(R.layout.fragment_create_info_chat) {

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

    private val permissionFragment by registerFragment(
        componentFragmentContractInterface = PermissionComponentContract
    )

    override val vm: CreateInfoViewModel by viewModel(
        getParamsInterface = CreateInfoContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::pickImageFragment)
            .register(::permissionFragment)
    )

    private val binding by fragmentViewBinding(FragmentCreateInfoChatBinding::bind)

    private val adapter = ContactsAdapter(onItemClicked = {

    }, selectActive = false, textColor = getTheme().text0Color())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        listContacts.adapter = adapter
        toolbar.setOnButtonClickListener {
            onBackPressed()
        }
        next.onClick {
            vm.onReadyClicked(chatName.text.toString())
        }
        chatName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                requireActivity().hideKeyboard(true)
                true
            } else {
                false
            }
        }
        chatName.doOnTextChanged { _, _, _, count ->
            if(count == 0) binding.next.hide() else binding.next.show()
        }
        chatAvatar.onClick {
            vm.onAvatarClicked()
        }
    }

    override fun syncTheme(appTheme: Theme) {
        appTheme.context = requireActivity().applicationContext
    }

    override fun renderState(state: CreateInfoState) {
        adapter.items = state.contacts
        state.avatarImage?.let { avatarImage ->
            with(binding.chatAvatar) {
                clipToOutline = true
                outlineProvider = RoundedCornersOutlineProvider(height.toFloat())
                setImageBitmap(avatarImage.bitmap)
            }
        }
    }

    override fun handleEvent(event: CreateInfoEvent) {
        when (event) {
            is CreateInfoEvent.OpenCamera -> {
                vm.selectFromCamera()
            }
            is CreateInfoEvent.OpenImagePicker -> {
                vm.selectFromGallery()
            }
        }
    }

}