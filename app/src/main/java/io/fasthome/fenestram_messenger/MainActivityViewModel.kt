package io.fasthome.fenestram_messenger

import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.main_api.MainFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.navigation.model.createParams
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * ViewModel для [MainActivity].
 * - открывает корневой экран при старте приложения;
 * - отвечает за запуск и обработку результата флоу авторизации;
 * - производит сброс на Главный экран, если юзер авторизован, и МП было свернуто
 *
 * Если юзер не авторизован, то в корне стека навигации ВСЕГДА должен быть экран [MainActivityViewModel.GuestModeRootNavigationContract].
 * Это нужно для того, чтобы экран [AuthFeature.authNavigationContract] не был в самом корне,
 * и чтобы при переходе "назад" с возвратом результата из [AuthFeature.authNavigationContract] не завершалась цепочка экранов
 * и не закрывалась [MainActivity].
 *
 * Если юзер авторизован, то в корне стека навигации ВСЕГДА должен быть Главный экран [MainFeature.navigationContract].
 *
 */
class MainActivityViewModel(
    router: ContractRouter,
    private val features: Features,
) : BaseViewModel<MainActivityState, Nothing>(requestParams = REQUEST_PARAMS, router = router) {

    class Features(
        val authFeature: AuthFeature,
        val mainFeature: MainFeature
    )

    private val authLauncher = registerScreen(features.authFeature.authNavigationContract) { result ->
        when (result) {
            AuthFeature.AuthResult.Canceled -> router.finishChain()
            AuthFeature.AuthResult.Success -> openAuthedRootScreen()
        }
    }

    init {
        features.authFeature.startAuthEvents
            .onEach {
                startAuth()
            }
            .launchIn(viewModelScope)
    }

    override fun createInitialState(): MainActivityState {
        return MainActivityState()
    }

    fun onAppStarted() {
        viewModelScope.launch {
            when (val isAuthedResult = features.authFeature.isUserAuthorized()) {
                is CallResult.Success -> when {
                    !isAuthedResult.data -> startAuth()
                    else -> openAuthedRootScreen()
                }
                is CallResult.Error -> startAuth()
            }
        }
    }

    private fun startAuth() {
        router.newRootScreen(GuestModeRootNavigationContract.createParams())
        authLauncher.launch(NoParams)
    }

    private fun openAuthedRootScreen() {
        router.newRootScreen(features.mainFeature.mainNavigationContract.createParams())
    }

    companion object {
        val REQUEST_PARAMS = RequestParams(
            requestKey = "MainActivityViewModel_requestKey",
            resultKey = ContractRouter.IGNORE_RESULT,
        )

        object GuestModeRootNavigationContract : NavigationContract<NoParams, NoResult>(Fragment::class)
    }
}