/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import com.dolatkia.animatedThemeManager.ThemeManager
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_impl.R
import io.fasthome.fenestram_messenger.profile_impl.databinding.FragmentProfileBinding
import io.fasthome.fenestram_messenger.profile_impl.presentation.profile.dialog.StatusSelectionDialog
import io.fasthome.fenestram_messenger.settings_api.SettingsFeature
import io.fasthome.fenestram_messenger.uikit.theme.DarkTheme
import io.fasthome.fenestram_messenger.uikit.theme.LightTheme
import io.fasthome.fenestram_messenger.uikit.theme.Theme
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
        activity?.window?.apply {
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        ivAvatar.onClick {
            vm.onAvatarClicked()
        }

        ibEditData.onClick {
            vm.editClicked()
        }

        status.onClick {
            vm.onStatusClicked()
        }

        nightTheme.setOnCheckedChangeListener { compoundButton, b ->
            if(b){
                ThemeManager.instance.changeTheme(LightTheme(), compoundButton)
            }else{
                ThemeManager.instance.changeTheme(DarkTheme(), compoundButton)
            }
        }
    }

    override fun syncTheme(appTheme: Theme) = with(binding){
        appTheme.setContext(requireActivity().applicationContext)
        gradient.background = appTheme.gradientDrawable()
        view.background = appTheme.shapeBg3_20dp()
        username.setTextColor(appTheme.text0Color())
        email.setTextColor(appTheme.text1Color())
        bgGeometry.background = appTheme.backgroundGeometry()
        collapsingToolbar.contentScrim = appTheme.bg0Color().toDrawable()
    }

    override fun renderState(state: ProfileState): Unit = with(binding) {
        state.username?.let { username.setPrintableText(it) }
        state.email?.let { email.setPrintableText(it) }
        state.avatarUrl?.let { url ->
            ivAvatar.loadCircle(
                url,
                placeholderRes = R.drawable.ic_avatar_placeholder,
                progressBar = progressBar
            )
        }
    }

    override fun handleEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.AvatarLoading -> {
                binding.progressBar.alpha = if (event.isLoading) 1f else 0f
            }
            is ProfileEvent.ShowStatusSelectionDialog -> {
                StatusSelectionDialog.create(
                    this@ProfileFragment,
                    statusItems = event.items,
                    onStatusSelected = {
                        vm.onStatusSelected(it)
                    }
                ).show()
            }
            is ProfileEvent.UpdateStatus -> {
                binding.status.backgroundTintList = ColorStateList.valueOf(event.status.dotColor)
                binding.status.setPrintableText(event.status.name)
            }
        }
    }

}