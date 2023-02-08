package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.fenestram_messenger.core.ui.dialog.AcceptDialog
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.core.ui.extensions.setContent
import io.fasthome.fenestram_messenger.group_guest_api.GroupGuestFeature
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.BottomSheetDismissListener
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.FragmentProfileGuestBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter.RecentFilesAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter.RecentImagesAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.EditTextStatus
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.TextViewKey
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.model.Bytes
import org.koin.android.ext.android.inject

class ProfileGuestFragment :
    BaseFragment<ProfileGuestState, ProfileGuestEvent>(R.layout.fragment_profile_guest), BottomSheetDismissListener {

    private val binding by fragmentViewBinding(FragmentProfileGuestBinding::bind)
    private val recentFilesAdapter = RecentFilesAdapter()
    private val recentImagesAdapter = RecentImagesAdapter(onMoreClicked = {
        vm.onShowPhotosClicked()
    })

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

    private val permissionInterface by registerFragment(PermissionComponentContract)

    override val vm: ProfileGuestViewModel by viewModel(
        getParamsInterface = ProfileGuestNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::groupParticipantsInterface)
            .register(::pickImageFragment)
            .register(::permissionInterface)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        recentFilesList.adapter = recentFilesAdapter
        recentImagesList.adapter = recentImagesAdapter
        recentImagesList.itemAnimator = null

        val flexboxManager =
            FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)

        recentImagesList.layoutManager = flexboxManager
        recentImagesList.supportBottomSheetScroll()

        recentFilesList.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        profileGuestName.imeOptions = EditorInfo.IME_ACTION_DONE
        profileGuestName.setRawInputType(InputType.TYPE_CLASS_TEXT)
        profileGuestName.addTextChangedListener { vm.onProfileNameChanged(it.toString()) }
        profileGuestName.onClick {
            vm.copyText(profileGuestName.text.toString(), TextViewKey.Name)
        }

        profileGuestNickname.onClick {
            vm.copyText(profileGuestNickname.text.toString(), TextViewKey.Nickname)
        }

        profileGuestPhone.onClick {
            vm.copyText(profileGuestPhone.text.toString(), TextViewKey.Phone)
        }

        recentFilesHeader.recentFilesShowAll.onClick {
            vm.onShowFilesClicked()
        }
        recentImagesHeader.imagesShowAll.onClick {
            vm.onShowPhotosClicked()
        }

        buttonDeleteChat.onClick {
            vm.onDeleteChatClicked()
        }

        profileGuestEdit.onClick {
            vm.onEditClicked(profileGuestName.text.toString().trim())
        }

        profileGuestAvatar.onClick {
            vm.onAvatarClicked()
        }
    }

    override fun renderState(state: ProfileGuestState) {
//        when {
//            state.recentFiles.size > 3 -> {
//                recentFilesAdapter.items = state.recentFiles.take(3)
//            }
//            state.recentImages.isNotEmpty() -> {
//                recentFilesAdapter.items = state.recentFiles
//            }
//            else -> {
//                //TODO Нет недавних файлов
//            }
//        }
//
//        when {
//            state.recentImages.size > 6 -> {
//                val items = state.recentImages.take(5) +
//                        RecentImagesViewItem(
//                            state.recentImages[5].image,
//                            state.recentImages.size - 5,
//                            true
//                        )
//                recentImagesAdapter.items = items
//            }
//            state.recentImages.isNotEmpty() -> {
//                recentImagesAdapter.items = state.recentImages
//            }
//            else -> {
//                //TODO Нет недавних изображений
//            }
//        }
        recentImagesAdapter.items = state.recentImages

        with(binding) {
            participantsContainer.isVisible = state.isGroup
            profileGuestPhone.isVisible = !state.isGroup
            profileGuestContainer.isVisible = !state.editMode
            pickPhotoIcon.isVisible = state.editMode && state.isGroup
            profileGuestName.isFocusable = state.editMode
            profileGuestName.isFocusableInTouchMode = state.editMode
            profileGuestName.isCursorVisible = state.editMode

            profileGuestName.background.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    when (state.profileGuestStatus) {
                        EditTextStatus.Idle -> {
                            profileGuestNameError.isVisible = false
                            R.color.dark1
                        }
                        EditTextStatus.Editable -> {
                            profileGuestNameError.isVisible = false
                            R.color.white
                        }
                        EditTextStatus.Error -> {
                            profileGuestNameError.isVisible = true
                            R.color.red
                        }
                    }
                )
            )

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
                if (state.isGroup)
                    profileGuestAvatar.brightness = 0.5F
                profileGuestEdit.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_complete_edit
                    )
                )
                if (state.profileGuestStatus == EditTextStatus.Idle) {
                    profileGuestName.setPrintableText(state.userName)
                    profileGuestName.background.setTint(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                }
            } else {
                profileGuestAvatar.brightness = 1F
                profileGuestEdit.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_edit
                    )
                )
                profileGuestPhone.setPrintableText(state.userPhone)
                profileGuestName.setPrintableText(state.userName)
                requireActivity().hideKeyboard(true)
            }

            when {
                state.avatarContent != null -> profileGuestAvatar.setContent(state.avatarContent, CircleCrop())
                state.userAvatar.isNotEmpty() -> profileGuestAvatar.loadCircle(
                    url = state.userAvatar,
                    placeholderRes = R.drawable.ic_avatar_placeholder
                )
                else -> profileGuestAvatar.loadCircle(R.drawable.ic_avatar_placeholder)
            }
            recentImagesHeader.recentImagesCount.setPrintableText(
                state.recentImagesCount
            )
            recentFilesHeader.recentFileCount.setPrintableText(
                state.recentFilesCount
            )
        }
    }

    override fun handleEvent(event: ProfileGuestEvent) {
        when (event) {
            is ProfileGuestEvent.DeleteChatEvent -> AcceptDialog.create(
                this,
                PrintableText.StringResource(R.string.common_delete_chat_dialog),
                vm::deleteChat,
                id = event.id
            )
                .show()
            is ProfileGuestEvent.Loading -> {
                binding.profileGuestEdit.loading(event.isLoading)
            }
            is ProfileGuestEvent.CopyText -> {
                copyTextToClipBoard(event.text, event.toastMessage)
            }
        }
    }

    override fun onDismiss() {
        vm.exitWithNameChanged()
    }

    private fun ImageButton.loading(isLoad: Boolean?) {
        if (isLoad == null) {
            return
        }
        binding.progress.isVisible = isLoad
        this.isEnabled = !isLoad
        this.isInvisible = isLoad
    }

}