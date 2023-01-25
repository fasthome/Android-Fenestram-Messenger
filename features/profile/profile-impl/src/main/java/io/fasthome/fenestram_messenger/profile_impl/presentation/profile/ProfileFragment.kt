/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_impl.R
import io.fasthome.fenestram_messenger.profile_impl.databinding.FragmentProfileBinding
import io.fasthome.fenestram_messenger.settings_api.SettingsFeature
import io.fasthome.fenestram_messenger.util.model.Bytes
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText
import org.koin.android.ext.android.inject

class ProfileFragment : BaseFragment<ProfileState, ProfileEvent>(R.layout.fragment_profile) {

    private val settingsFeature: SettingsFeature by inject()

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

    private val settingsFragment by registerFragment(
        componentFragmentContractInterface = settingsFeature.settingsComponentContract,
        paramsProvider = {
            NoParams
        },
        containerViewId = R.id.settings_container
    )


    override val vm: ProfileViewModel by viewModel(
        getParamsInterface = ProfileNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::pickImageFragment)
            .register(::settingsFragment)
    )

    private val binding by fragmentViewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        ivAvatar.onClick {
            vm.onAvatarClicked()
        }

        ibEditData.onClick {
            vm.editClicked()
        }
    }

    override fun renderState(state: ProfileState): Unit = with(binding) {
        state.username?.let { username.setPrintableText(it) }
        state.nickname?.let { nickname.setPrintableText(it) }
        state.birth?.let { birth.setPrintableText(it) }
        state.email?.let { email.setPrintableText(it) }
        state.avatarUrl?.let { url ->
            ivAvatar.loadCircle(url, placeholderRes = R.drawable.ic_avatar_placeholder)
        }
    }

    override fun handleEvent(event: ProfileEvent) = noEventsExpected()

}