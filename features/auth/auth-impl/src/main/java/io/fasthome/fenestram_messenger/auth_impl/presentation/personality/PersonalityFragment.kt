package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import io.fasthome.component.personality_data.PersonalityComponentContract
import io.fasthome.component.personality_data.PersonalityParams
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.component.select_from.SelectFromDialog
import io.fasthome.fenestram_messenger.auth_api.UserDetail
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentPersonalityBinding
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.core.ui.extensions.setContent
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.model.Bytes
import io.fasthome.fenestram_messenger.util.model.Bytes.Companion.BYTES_PER_MB
import io.fasthome.fenestram_messenger.util.setPrintableText

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
                        id = 0,
                        phone = "",
                        profileImageUrl = null,
                        name = this.name,
                        email = this.email,
                        birth = this.birth,
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
        labelWelcome.setPrintableText(state.label)
        buttonReady.isEnabled = state.readyEnabled
        if (state.profileImageUrl == null && state.avatarContent == null) {
            userPhoto.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_avatar_placeholder
                )
            )
        } else {
            state.profileImageUrl?.let { url ->
                userPhoto.loadCircle(url, R.drawable.ic_avatar_placeholder)
            }
            state.avatarContent?.let { content ->
                userPhoto.setContent(content, CircleCrop())
            }
        }
    }

    override fun handleEvent(event: PersonalityEvent) {
        when (event) {
            PersonalityEvent.LaunchGallery -> Unit
            is PersonalityEvent.Loading -> {
                binding.buttonReady.loading(event.isLoading)
            }
            PersonalityEvent.ShowSelectFromDialog -> {
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

    private fun Button.loading(isLoad: Boolean?) {
        if (isLoad == null) {
            return
        }
        binding.progress.isVisible = isLoad
        this.isEnabled = !isLoad
        if (isLoad) {
            this.text = ""
        } else {
            this.setText(R.string.auth_ready_button)
        }
    }

    enum class EditTextKey {
        NameKey,
        UserNameKey,
        BirthdateKey,
        MailKey
    }
}