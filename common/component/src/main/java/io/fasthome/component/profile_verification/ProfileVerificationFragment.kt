package io.fasthome.component.profile_verification

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import io.fasthome.component.R
import io.fasthome.component.databinding.FragmentProfileVerificationBinding
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.fenestram_messenger.core.ui.extensions.loadAvatarWithGradient
import io.fasthome.fenestram_messenger.core.ui.extensions.setContent
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.getPrintableText
import io.fasthome.fenestram_messenger.util.model.Bytes
import io.fasthome.fenestram_messenger.util.onClick

class ProfileVerificationFragment :
    BaseFragment<ProfileVerificationState, ProfileVerificationEvent>(R.layout.fragment_profile_verification) {

    private val binding by fragmentViewBinding(FragmentProfileVerificationBinding::bind)

    override val vm: ProfileVerificationViewModel by viewModel(
        getParamsInterface = ProfileVerificationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::pickImageFragment)
            .register(::permissionInterface)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnReady.onClick {
            vm.onDoneClicked()
        }
        binding.ivEditAvatar.onClick {
            vm.onEditAvatarClicked()
        }
        binding.profileAvatar.onClick {
            vm.onImageClicked()
        }
    }

    override fun syncTheme(appTheme: Theme) {
        with(binding) {
            appTheme.context = requireActivity().applicationContext
            bgGeometry.background = appTheme.backgroundGeometry()
            val bgColorStateList = ColorStateList.valueOf(appTheme.stroke2Color())
            tvName.setTextColor(appTheme.text0Color())
            tvName.backgroundTintList = bgColorStateList
            tvEmail.setTextColor(appTheme.text0Color())
            tvEmail.backgroundTintList = bgColorStateList
            tvTitle.setTextColor(appTheme.text0Color())
            tvDepartment.setTextColor(appTheme.text0Color())
            tvDepartment.backgroundTintList = bgColorStateList
            tvBirthday.setTextColor(appTheme.text0Color())
            tvBirthday.backgroundTintList = bgColorStateList
            tvSpeciality.setTextColor(appTheme.text0Color())
            tvSpeciality.backgroundTintList = bgColorStateList
        }
    }

    override fun handleEvent(event: ProfileVerificationEvent) {
        when (event) {
            is ProfileVerificationEvent.OpenCamera -> {
                vm.selectFromCamera()
            }
            is ProfileVerificationEvent.OpenImagePicker -> {
                vm.selectFromGallery()
            }
        }
    }

    override fun renderState(state: ProfileVerificationState) {
        with(binding) {
            state.email?.let { binding.tvEmail.text = getPrintableText(it) }
            state.speciality?.let { binding.tvSpeciality.text = getPrintableText(it) }
            state.birthday?.let { binding.tvBirthday.text = getPrintableText(it) }
            state.userName?.let { binding.tvName.text = getPrintableText(it) }
            state.department?.let { binding.tvDepartment.text = getPrintableText(it) }

            when {
                state.avatarContent != null -> profileAvatar.setContent(
                    state.avatarContent,
                    CircleCrop()
                )
                state.userAvatar.isNullOrEmpty() -> {
                    if (state.userName != null)
                        profileAvatar.loadAvatarWithGradient(
                            url = null,
                            username = getPrintableText(state.userName)
                        )
                }
                else -> {
                    if (state.userName != null)
                        profileAvatar.loadAvatarWithGradient(
                            url = state.userAvatar,
                            username = getPrintableText(state.userName)
                        )
                }
            }
        }
    }

}