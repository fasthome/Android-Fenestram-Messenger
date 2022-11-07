/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import io.fasthome.component.personality_data.PersonalityComponentContract
import io.fasthome.component.personality_data.PersonalityParams
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_impl.R
import io.fasthome.fenestram_messenger.profile_impl.databinding.FragmentProfileBinding
import io.fasthome.fenestram_messenger.util.model.Bytes
import io.fasthome.fenestram_messenger.util.onClick

class ProfileFragment : BaseFragment<ProfileState, ProfileEvent>(R.layout.fragment_profile) {

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

    private val personalityDataFragment by registerFragment(
        componentFragmentContractInterface = PersonalityComponentContract,
        paramsProvider = {
            PersonalityParams(null, false, false)
        },
        containerViewId = R.id.personality_data_container
    )


    override val vm: ProfileViewModel by viewModel(
        getParamsInterface = ProfileNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::pickImageFragment)
            .register(::personalityDataFragment)
    )

    private val binding by fragmentViewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        vm.onViewCreated()

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        ivAvatar.onClick {
            vm.onAvatarClicked()
        }

        ibAdd.onClick {
            vm.onAvatarClicked()
        }

        ibEditData.onClick {
            vm.editClicked()
        }
        bCancel.onClick {
            vm.cancelClicked()
        }
        ibSettings.onClick {
            vm.startSettings()
        }

        bDone.onClick {
            vm.checkPersonalData()
        }

        vm.fetchProfile()
    }

    override fun renderState(state: ProfileState): Unit = with(binding) {
        bDone.isEnabled = state.readyEnabled
        username.text = state.username
        username.isVisible = !state.isEdit
        if (state.avatarUrl == null && state.avatarBitmap == null) {
            ivAvatar.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_avatar_placeholder
                )
            )
        } else {
            state.avatarUrl?.let { url ->
                ivAvatar.loadCircle(url, placeholderRes = R.drawable.ic_avatar_placeholder)
            }

            state.avatarBitmap?.let { bitmap ->
                ivAvatar.loadCircle(bitmap, placeholderRes = R.drawable.ic_avatar_placeholder)
            }
        }

        llButtons.isVisible = state.isEdit
        ibAdd.isVisible = state.isEdit
        progress.isVisible = state.isLoad
        bCancel.isVisible = !state.isLoad
        bDone.isVisible = !state.isLoad
    }

    override fun handleEvent(event: ProfileEvent) = noEventsExpected()

}