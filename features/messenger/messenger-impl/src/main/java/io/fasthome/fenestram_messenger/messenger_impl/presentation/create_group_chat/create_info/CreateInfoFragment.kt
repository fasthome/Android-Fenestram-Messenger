/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.component.select_from.SelectFromDialog
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentCreateInfoChatBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.adapter.ContactsAdapter
import io.fasthome.fenestram_messenger.mvi.BaseViewEvent
import io.fasthome.fenestram_messenger.mvi.ViewModelInterface
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.RoundedCornersOutlineProvider
import io.fasthome.fenestram_messenger.util.dp
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

    override val vm: CreateInfoViewModel by viewModel(
        getParamsInterface = CreateInfoContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::pickImageFragment)
    )

    private val binding by fragmentViewBinding(FragmentCreateInfoChatBinding::bind)

    private val adapter = ContactsAdapter(onItemClicked = {

    }, selectActive = false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        listContacts.adapter = adapter
        toolbar.setOnButtonClickListener {
            onBackPressed()
        }
        next.onClick {
            vm.onReadyClicked(chatName.text.toString())
        }
        chatAvatar.onClick {
            vm.onAvatarClicked()
        }
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
            is CreateInfoEvent.ShowSelectFromDialog -> {
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
    }

}