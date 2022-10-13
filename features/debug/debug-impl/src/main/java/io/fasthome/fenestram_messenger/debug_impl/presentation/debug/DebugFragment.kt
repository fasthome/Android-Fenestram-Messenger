/**
 * Created by Dmitry Popov on 18.07.2022.
 */
package io.fasthome.fenestram_messenger.debug_impl.presentation.debug

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import com.jakewharton.processphoenix.ProcessPhoenix
import io.fasthome.component.person_detail.PersonDetailDialog
import io.fasthome.fenestram_messenger.core.debug.EndpointsConfig
import io.fasthome.fenestram_messenger.core.ui.dialog.AcceptDialog
import io.fasthome.fenestram_messenger.debug_impl.R
import io.fasthome.fenestram_messenger.debug_impl.databinding.FragmentDebugBinding
import io.fasthome.fenestram_messenger.mvi.Message
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.showMessage
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onClick

class DebugFragment : BaseFragment<DebugState, DebugEvent>(R.layout.fragment_debug) {

    private val binding by fragmentViewBinding(FragmentDebugBinding::bind)

    override val vm: DebugViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        debugFeatures.onClick {
            vm.onFeaturesClicked()
        }

        debugComponents.onClick {
            vm.onComponentsClicked()
        }

        debugRequests.onClick {
            vm.onRequestsClicked()
        }

        debugLogin.onClick {
            vm.onLoginClicked()
        }

        debugLoginAccept.onClick {
            vm.onLoginAcceptClicked(
                phone = phoneInput.text.toString(),
                code = codeInput.text.toString()
            )
        }

        debugAuth.onClick {
            vm.onAuthClicked()
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

        debugPersonDetailDialog.onClick {
            vm.onPersonDetailClicked()
        }

        linkField.onClick {
            vm.onLinkFieldClicked(linkField.text.toString())
        }
        serverDevelop.onClick {
            vm.onEnvironmentChangedClicked(EndpointsConfig.Dev)
        }
        serverProd.onClick {
            vm.onEnvironmentChangedClicked(EndpointsConfig.Prod)
        }

    }

    override fun renderState(state: DebugState) = with(binding) {
        featuresContainer.isVisible = state.featuresVisible
        requestsContainer.isVisible = state.requestsVisible
        componentsContainer.isVisible = state.componentsVisible
        loginContainer.isVisible = state.loginVisible
        linkField.text = state.token
        userIdField.text = state.userId
        phoneInput.setText(state.userPhone)
        codeInput.setText(state.userCode)
        serverDevelop.isSelected = state.selectedEnv == EndpointsConfig.Dev
        serverProd.isSelected = state.selectedEnv == EndpointsConfig.Prod
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
            is DebugEvent.AcceptEnvChangeDialog -> AcceptDialog.create(
                fragment = this,
                titleText = PrintableText.StringResource(R.string.debug_rebirth_endpoints),
                accept = { vm.onEnvironmentChanged(event.endpointsConfig) },
                id = 0,
                acceptButtonRes = R.string.common_accept
            ).show()
            is DebugEvent.ShowPersonDetailDialog ->
                PersonDetailDialog
                    .create(
                        fragment = this,
                        personDetail = event.selectedPerson,
                        launchFaceCallClicked = {
                            //TODO
                        },
                        launchCallClicked = {
                            //TODO
                        },
                        launchConversationClicked = {
                        },
                        onAvatarClicked = {
                            // nothing
                        })
                    .show()
            DebugEvent.RebirthApplication -> ProcessPhoenix.triggerRebirth(context)
        }
    }
}