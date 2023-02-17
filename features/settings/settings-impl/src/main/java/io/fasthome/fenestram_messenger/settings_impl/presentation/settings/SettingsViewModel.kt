package io.fasthome.fenestram_messenger.settings_impl.presentation.settings


import androidx.lifecycle.viewModelScope
import com.instabug.library.Instabug
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.settings_api.SettingsInterface
import io.fasthome.fenestram_messenger.settings_impl.R
import io.fasthome.fenestram_messenger.settings_impl.domain.repo.SettingsRepo
import io.fasthome.fenestram_messenger.settings_impl.presentation.infoapp.InfoappNavigationContact
import io.fasthome.fenestram_messenger.settings_impl.presentation.settings.model.SettingsViewItem
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.coroutines.launch


class SettingsViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val features: Features,
    private val settingsRepo: SettingsRepo
) : BaseViewModel<SettingsState, SettingsEvent>(router, requestParams), SettingsInterface {

    class Features(
        val authFeature: AuthFeature,
    )

    private val infoappLauncher = registerScreen(InfoappNavigationContact)

    override fun createInitialState(): SettingsState {
        return SettingsState(items = createSettingsList())
    }

    private fun createSettingsList(): List<SettingsViewItem> = listOf(
        SettingsViewItem(
            icon = R.drawable.ic_info,
            title = PrintableText.StringResource(R.string.settings_about_app),
            onItemClicked = {
                infoappLauncher.launch()
            },
        ),
        SettingsViewItem(
            icon = R.drawable.ic_delete_account,
            title = PrintableText.StringResource(R.string.settings_delete_account),
            onItemClicked = {
                onDeleteAccountClicked()
            },
        ),
        SettingsViewItem(
            icon = R.drawable.ic_info,
            title = PrintableText.StringResource(R.string.settings_bug_report),
            onItemClicked = {
                Instabug.show()
            },
        ),
        SettingsViewItem(
            icon = R.drawable.ic_exit,
            title = PrintableText.StringResource(R.string.settings_logout),
            onItemClicked = {
                onLogoutClicked()
            },
        ),
    )
    fun onLogoutClicked() {
        sendEvent(SettingsEvent.Logout)
    }

    fun logout(needRequest: Boolean = true) {
        viewModelScope.launch {
            features.authFeature.logout(needRequest).withErrorHandled {}
        }
    }

    fun startInfoapp() {
        infoappLauncher.launch(NoParams)
    }

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }

    fun onDeleteAccountClicked() {
        sendEvent(SettingsEvent.DeleteAccount)
    }

    fun deleteAccount(){
        viewModelScope.launch {
            settingsRepo.deleteAccount().withErrorHandled(ShowErrorType.Dialog) {
                logout(false)
            }
        }
    }
}