/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import coil.load
import coil.transform.CircleCropTransformation
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_impl.R
import io.fasthome.fenestram_messenger.profile_impl.databinding.FragmentProfileBinding
import io.fasthome.fenestram_messenger.profile_impl.presentation.profile.ProfileState.EditTextKey
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.model.Bytes

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

    override val vm: ProfileViewModel by viewModel(
        getParamsInterface = ProfileNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::pickImageFragment)
    )

    private val binding by fragmentViewBinding(FragmentProfileBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        ivAvatar.onClick {
            vm.onAvatarClicked()
        }
        ibEditData.onClick {
            vm.editClicked()
        }
        bCancel.onClick {
            vm.cancelClicked()
        }
        bDone.onClick {
            vm.cancelClicked()
        }
        ibSettings.onClick {
            vm.startSettings()
        }

        labelNicknameU.includeTextView.setPrintableText(PrintableText.StringResource(R.string.common_user_name_label))
        labelHBDay.includeTextView.setPrintableText(PrintableText.StringResource(R.string.common_birthday_label))
        labelEmail.includeTextView.setPrintableText(PrintableText.StringResource(R.string.common_email_label))
    }

    override fun renderState(state: ProfileState): Unit = with(binding) {
        state.fieldsData.forEach { field ->
            when (field.key) {
                EditTextKey.NicknameKey -> nickContainer.includeEditText
                EditTextKey.BirthdateKey -> hbDayContainer.includeEditText
                EditTextKey.MailKey -> emailContainer.includeEditText
                EditTextKey.UsernameKey -> username
            }.let { view ->
                view.setPrintableText(field.text)
            }
        }

        state.avatarBitmap?.let { bitmap ->
            ivAvatar.load(bitmap) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.ic_baseline_account_circle_24)
            }
        }

        state.avatarUrl?.let { url ->
            ivAvatar.load(url) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.ic_baseline_account_circle_24)
            }
        }

        llButtons.isVisible = state.isEdit

        nickContainer.includeEditText.isEnabled = state.isEdit
        emailContainer.includeEditText.isEnabled = state.isEdit
        hbDayContainer.includeEditText.isEnabled = state.isEdit
    }

    override fun handleEvent(event: ProfileEvent) = noEventsExpected()

}