/**
 * Created by Dmitry Popov on 18.07.2022.
 */
package io.fasthome.fenestram_messenger.debug_impl.presentation.debug

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.View
import android.widget.Toast
import io.fasthome.fenestram_messenger.debug_impl.R
import io.fasthome.fenestram_messenger.debug_impl.databinding.FragmentDebugBinding
import io.fasthome.fenestram_messenger.mvi.Message
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.showMessage
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onClick

class DebugFragment : BaseFragment<DebugState, DebugEvent>(R.layout.fragment_debug) {

    private val binding by fragmentViewBinding(FragmentDebugBinding::bind)

    override val vm: DebugViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        debugAuth.onClick {
            vm.onAuthClicked()
        }

        debugSocket.onClick {
            vm.onSocketClicked()
        }

        debugProfileGuest.onClick {
            vm.onProfileGuestClicked()
        }

        debugLogout.onClick {
            vm.onLogoutClicked()
        }

        debugPersonalData.onClick {
            vm.onPersonalDataClicked()
        }

        debugErrorDialog.onClick {
            vm.onErrorDialogClicked()
        }

        debugDeleteContacts.onClick {
            vm.onDeleteContactsClicked()
        }

        debugGroupGuest.onClick {
            vm.onGroupGuestClicked()
        }

        debugSendPush.onClick {
            vm.sendTestPushNotification()
        }

        debugUpdatePush.onClick {
            vm.updatePushToken()
        }

        debugOnboarding.onClick {
            vm.onOnboardingClicked()
        }

        linkField.onClick {
            vm.onLinkFieldClicked(linkField.text.toString())
        }

    }

    override fun renderState(state: DebugState) {
        with(binding){
            linkField.text = state.token
        }
    }
    override fun handleEvent(event: DebugEvent) {
        when (event) {
            is DebugEvent.ContactsDeleted -> {
                showMessage(
                    Message.PopUp(
                        messageText = PrintableText.Raw("Контакты успешно удалены")
                    )
                )
            }
            is DebugEvent.CopyTokenEvent -> {
                (requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                    ClipData.newPlainText("copy", event.token.replace("\\s".toRegex(), ""))
                )
                Toast.makeText(requireContext(), "Токен скопирован", Toast.LENGTH_SHORT).show()
            }
        }
    }
}