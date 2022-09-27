package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_edit

import android.os.Bundle
import android.view.View
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.FragmentProfileGuestEditBinding
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.model.Bytes
import io.fasthome.fenestram_messenger.util.setPrintableText

class ProfileGuestEditFragment :
    BaseFragment<ProfileGuestEditState, ProfileGuestEditEvent>(R.layout.fragment_profile_guest_edit) {

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

    private val binding by fragmentViewBinding(FragmentProfileGuestEditBinding::bind)

    override val vm: ProfileGuestEditViewModel by viewModel(
        getParamsInterface = ProfileGuestEditNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::pickImageFragment)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            editComplete.setOnClickListener {
                vm.onEditCompletedClick(chatName.text.toString().trim())
            }

            avatar.setOnClickListener {
                vm.onAvatarClicked()
            }
        }
    }

    override fun renderState(state: ProfileGuestEditState) = with(binding) {
        chatName.setText(state.chatName)

        when {
            state.avatarBitmap != null -> avatar.loadCircle(
                bitmap = state.avatarBitmap,
                placeholderRes = R.drawable.bg_account_circle
            )
            state.avatarUrl.isNotEmpty() -> avatar.loadCircle(
                url = state.avatarUrl,
                placeholderRes = R.drawable.bg_account_circle
            )
        }

        participantsQuantity.setPrintableText(
            PrintableText.PluralResource(
                R.plurals.chat_participants_count,
                state.participantsQuantity,
                state.participantsQuantity
            )
        )
    }

    override fun handleEvent(event: ProfileGuestEditEvent) = noEventsExpected()

}