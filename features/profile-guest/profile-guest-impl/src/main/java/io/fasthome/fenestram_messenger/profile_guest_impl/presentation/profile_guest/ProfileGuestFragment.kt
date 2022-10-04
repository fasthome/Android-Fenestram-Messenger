package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.fenestram_messenger.core.ui.dialog.DeleteChatDialog
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.group_guest_api.GroupGuestFeature
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.FragmentProfileGuestBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter.RecentFilesAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter.RecentImagesAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getPrintableRawText
import io.fasthome.fenestram_messenger.util.model.Bytes
import io.fasthome.fenestram_messenger.util.setPrintableText
import org.koin.android.ext.android.inject

class ProfileGuestFragment :
    BaseFragment<ProfileGuestState, ProfileGuestEvent>(R.layout.fragment_profile_guest) {

    private val binding by fragmentViewBinding(FragmentProfileGuestBinding::bind)
    private val recentFilesAdapter = RecentFilesAdapter()
    private val recentImagesAdapter = RecentImagesAdapter()

    private val groupGuestFeature: GroupGuestFeature by inject()

    private val groupParticipantsInterface by registerFragment(
        componentFragmentContractInterface = groupGuestFeature.groupGuestComponentContract,
        paramsProvider = { ProfileGuestNavigationContract.getParams.getParams(this).groupParticipantsParams },
        containerViewId = R.id.participants_container
    )

    private val pickImageFragment by registerFragment(
        componentFragmentContractInterface = PickFileComponentContract,
        paramsProvider = {
            PickFileComponentParams(
                mimeType = PickFileComponentParams.MimeType.Image(compressToSize = Bytes(Bytes.BYTES_PER_MB))
            )
        }
    )

    override val vm: ProfileGuestViewModel by viewModel(
        getParamsInterface = ProfileGuestNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::groupParticipantsInterface)
            .register(::pickImageFragment)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        recentFilesList.adapter = recentFilesAdapter
        recentImagesList.adapter = recentImagesAdapter
        recentImagesList.layoutManager = GridLayoutManager(context, 3)

        recentFilesList.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

//        vm.fetchFilesAndPhotos()

        recentFilesHeader.recentFilesShowAll.setOnClickListener {
            vm.onShowFilesClicked()
        }
        recentImagesHeader.imagesShowAll.setOnClickListener {
            vm.onShowPhotosClicked()
        }

        buttonDeleteChat.setOnClickListener {
            vm.onDeleteChatClicked()
        }

        profileGuestEditGroup.setOnClickListener {
            vm.onEditGroupClicked(profileGuestName.text.toString())
        }

        profileGuestAvatar.setOnClickListener {
            vm.onAvatarClicked()
        }
    }

    override fun renderState(state: ProfileGuestState) {
        when {
            state.recentFiles.size > 3 -> {
                recentFilesAdapter.items = state.recentFiles.take(3)
            }
            state.recentImages.isNotEmpty() -> {
                recentFilesAdapter.items = state.recentFiles
            }
            else -> {
                //TODO Нет недавних файлов
            }
        }

        when {
            state.recentImages.size > 6 -> {
                val items = state.recentImages.take(5) +
                        RecentImagesViewItem(
                            state.recentImages[5].image,
                            state.recentImages.size - 5,
                            true
                        )
                recentImagesAdapter.items = items
            }
            state.recentImages.isNotEmpty() -> {
                recentImagesAdapter.items = state.recentImages
            }
            else -> {
                //TODO Нет недавних изображений
            }
        }

        with(binding) {
            profileGuestEditGroup.isVisible = state.isGroup
            participantsContainer.isVisible = state.isGroup
            profileGuestPhone.isVisible = !state.isGroup
            profileGuestContainer.isVisible = !state.editMode
            profileGuestVideoChat.isVisible = !state.editMode
            profileGuestCall.isVisible = !state.editMode
            pickPhotoIcon.isVisible = state.editMode
            profileGuestName.isEnabled = state.editMode
            profileGuestName.setPrintableText(state.userName)

            if (state.isGroup) {
                profileGuestNickname.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray1
                    )
                )
                profileGuestNickname.textSize = 14F
                profileGuestNickname.setPrintableText(
                    PrintableText.PluralResource(
                        R.plurals.chat_participants_count,
                        state.participantsQuantity,
                        state.participantsQuantity
                    )
                )
            } else {
                profileGuestNickname.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                profileGuestNickname.textSize = 18F
                profileGuestNickname.isVisible =
                    getPrintableRawText(state.userNickname).isNotEmpty()
                profileGuestNickname.setPrintableText(
                    PrintableText.Raw("@" + getPrintableRawText(state.userNickname))
                )
            }

            if (state.editMode) {
                profileGuestAvatar.brightness = 0.5F
                profileGuestEditGroup.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_complete_edit
                    )
                )
            } else {
                profileGuestAvatar.brightness = 1F
                profileGuestEditGroup.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_edit
                    )
                )
                profileGuestPhone.setPrintableText(state.userPhone)
            }

            when {
                state.avatarBitmap != null -> profileGuestAvatar.loadCircle(
                    bitmap = state.avatarBitmap,
                    placeholderRes = R.drawable.common_avatar
                )
                state.userAvatar.isNotEmpty() -> profileGuestAvatar.loadCircle(
                    url = state.userAvatar,
                    placeholderRes = R.drawable.common_avatar
                )
                else -> profileGuestAvatar.loadCircle(R.drawable.common_avatar)
            }

            recentFilesHeader.recentFileCount.setPrintableText(
                PrintableText.PluralResource(
                    R.plurals.file_quantity,
                    state.recentFiles.size,
                    state.recentFiles.size
                )
            )
            recentImagesHeader.recentImagesCount.setPrintableText(
                PrintableText.PluralResource(
                    R.plurals.image_quantity,
                    state.recentImages.size,
                    state.recentImages.size
                )
            )
        }
    }

    override fun handleEvent(event: ProfileGuestEvent) {
        when (event) {
            is ProfileGuestEvent.DeleteChatEvent -> DeleteChatDialog.create(
                this,
                PrintableText.StringResource(R.string.common_delete_chat_dialog),
                vm::deleteChat,
                id = event.id
            )
                .show()
        }
    }

}