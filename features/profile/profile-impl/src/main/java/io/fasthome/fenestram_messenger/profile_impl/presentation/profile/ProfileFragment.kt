/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import coil.load
import coil.transform.CircleCropTransformation
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_impl.R
import io.fasthome.fenestram_messenger.profile_impl.databinding.FragmentProfileBinding
import io.fasthome.fenestram_messenger.util.*

class ProfileFragment : BaseFragment<ProfileState, ProfileEvent>(R.layout.fragment_profile) {

    private val permissionInterface by registerFragment(PermissionComponentContract)
    override val vm: ProfileViewModel by viewModel(
        getParamsInterface = ProfileNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::permissionInterface)
    )
    private val galleryLauncher = resultLauncher { result ->
        val data: Uri? = result.data?.data
        vm.onUpdatePhoto(data)
    }

    private val binding by fragmentViewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        ivAvatar.increaseHitArea(8.dp)
        ivAvatar.onClick{ vm.requestPermissionAndLoadPhoto() }

        ibEditData.setOnClickListener {
            vm.editClicked()
        }
        bCancel.setOnClickListener {
            vm.cancelClicked()
        }
        bDone.setOnClickListener {
            vm.cancelClicked()
        }
        ibSettings.setOnClickListener{
            vm.startSettings()
        }

        labelNicknameU.includeTextView.setPrintableText(PrintableText.StringResource(R.string.common_user_name_label))
        labelHBDay.includeTextView.setPrintableText(PrintableText.StringResource(R.string.common_birthday_label))
        labelEmail.includeTextView.setPrintableText(PrintableText.StringResource(R.string.common_email_label))
    }

    override fun renderState(state: ProfileState): Unit = with(binding) {
        ivAvatar.load(state.avatar) {
            transformations(CircleCropTransformation())
            placeholder(R.drawable.ic_baseline_account_circle_24)
        }

        llButtons.isVisible = state.isEdit

        nickContainer.includeEditText.isEnabled = state.isEdit
        emailContainer.includeEditText.isEnabled = state.isEdit
        hbDayContainer.includeEditText.isEnabled = state.isEdit
    }

    override fun handleEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LaunchGallery -> {
                galleryLauncher.launch(getGalleryIntent())
            }
        }
    }

}