package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import io.fasthome.component.personality_data.PersonalityParams
import io.fasthome.component.personality_data.PersonalityComponentContract
import io.fasthome.component.personality_data.UserDetail
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentPersonalityBinding
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.User
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.model.Bytes
import io.fasthome.fenestram_messenger.util.model.Bytes.Companion.BYTES_PER_MB

class PersonalityFragment :
    BaseFragment<PersonalityState, PersonalityEvent>(R.layout.fragment_personality) {

    private val pickImageFragment by registerFragment(
        componentFragmentContractInterface = PickFileComponentContract,
        paramsProvider = {
            PickFileComponentParams(
                mimeType = PickFileComponentParams.MimeType.Image(
                    compressToSize = Bytes(
                        BYTES_PER_MB
                    )
                )
            )
        }
    )
    private val personalityDataFragment by registerFragment(
        componentFragmentContractInterface = PersonalityComponentContract,
        paramsProvider = {
            with(PersonalityNavigationContract.getParams.getParams(this).userDetail) {
                PersonalityParams(
                    UserDetail(
                        name = this.name,
                        mail = this.email,
                        birthday = this.birth,
                        nickname = this.nickname
                    ),
                    nameVisible = true,
                    visibilityIcons = true
                )
            }
        },
        containerViewId = R.id.personality_data_container
    )

    override val vm: PersonalityViewModel by viewModel(
        getParamsInterface = PersonalityNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::pickImageFragment)
            .register(::personalityDataFragment),
    )

    private val binding by fragmentViewBinding(FragmentPersonalityBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        with(binding) {
            buttonReady.setOnClickListener {
                vm.checkPersonalData()
            }

            labelSkip.setOnClickListener {
                vm.skipPersonalData()
            }

            userPhoto.setOnClickListener {
                vm.onSelectPhotoClicked()
            }

            userPhotoAdd.setOnClickListener {
                vm.onSelectPhotoClicked()
            }
        }
    }

    override fun renderState(state: PersonalityState): Unit = with(binding) {
        buttonReady.isEnabled = state.readyEnabled
        if (state.profileImageUrl == null && state.avatarBitmap == null) {
            userPhoto.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_account_circle_24
                )
            )
        } else {
            state.profileImageUrl?.let { url ->
                userPhoto.loadCircle(url, R.drawable.ic_baseline_account_circle_24)
            }
            state.avatarBitmap?.let { bitmap ->
                userPhoto.loadCircle(bitmap, R.drawable.ic_baseline_account_circle_24)
            }
        }
    }

    override fun handleEvent(event: PersonalityEvent) = noEventsExpected()

    enum class EditTextKey {
        NameKey,
        UserNameKey,
        BirthdateKey,
        MailKey
    }
}