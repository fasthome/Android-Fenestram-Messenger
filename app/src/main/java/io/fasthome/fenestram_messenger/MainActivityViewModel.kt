package io.fasthome.fenestram_messenger

import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.core.exceptions.InternetConnectionException
import io.fasthome.fenestram_messenger.main_api.MainFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.*
import io.fasthome.fenestram_messenger.onboarding_api.OnboardingFeature
import io.fasthome.fenestram_messenger.profile_api.ProfileFeature
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.getOrDefault
import io.fasthome.fenestram_messenger.util.kotlin.getAndSet
import kotlinx.coroutines.flow.first
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
    private val deepLinkNavigator: DeepLinkNavigator,
) : BaseViewModel<MainActivityState, Nothing>(requestParams = REQUEST_PARAMS, router = router) {

    class Features(
        val authFeature: AuthFeature,
        val profileFeature: ProfileFeature,
        val mainFeature: MainFeature,
        val onboardingFeature: OnboardingFeature
    )

    private val authLauncher =
        registerScreen(features.authFeature.authNavigationContract) { result ->
            when (result) {
                AuthFeature.AuthResult.Canceled -> router.finishChain()
                AuthFeature.AuthResult.Success -> onboardingLauncher.launch()
            }
        }

    private val onboardingLauncher =
        registerScreen(features.onboardingFeature.onboardingNavigationContract) {
            openAuthedRootScreen()
        }

    private val personalityLauncher =
        registerScreen(features.authFeature.personalDataNavigationContract) { result ->
            when (result) {
                AuthFeature.AuthResult.Canceled -> router.finishChain()
                AuthFeature.AuthResult.Success -> openAuthedRootScreen()
            }
        }

    private var deepLinkResult: IDeepLinkResult? = null
        set(value) {
            field = value
            handleDeepLink()
        }

    init {
        features.authFeature.startAuthEvents
            .onEach {
                startAuth()
            }
            .launchIn(viewModelScope)
    }

    override fun createInitialState(): MainActivityState {
        return MainActivityState(false)
    }

    fun onAppStarted() {
        viewModelScope.launch {
            when (val isAuthedResult = features.authFeature.isUserAuthorized()) {
                is CallResult.Success -> when {
                    !isAuthedResult.data -> startAuth()
                    else -> checkPersonalData()
                }
                is CallResult.Error -> startAuth()
            }
        }
    }

    private suspend fun checkPersonalData() {
        when (val personalDataResult = features.profileFeature.getPersonalData()) {
            is CallResult.Success -> {
                with(personalDataResult.data) {
                    if (username != null && nickname != null && birth != null && email != null) {
                        openAuthedRootScreen()
                    } else {
                        openPersonalityScreen(
                            AuthFeature.PersonalDataParams(
                                username = username,
                                nickname = nickname,
                                birth = birth,
                                email = email,
                                avatar = avatar,
                            )
                        )
                    }
                }
            }
            is CallResult.Error -> {
                when (personalDataResult.error) {
                    is InternetConnectionException -> openAuthedRootScreen()
                    else -> startAuth()
                }
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

    private fun openPersonalityScreen(personalDataParams: AuthFeature.PersonalDataParams) {
        personalityLauncher.launch(personalDataParams)
    }

    fun handleDeepLinkResult(deepLinkResult: IDeepLinkResult) {
        viewModelScope.launch {
            if (features.authFeature.isUserAuthorized().getOrDefault(false)) {
                this@MainActivityViewModel.deepLinkResult = deepLinkResult
            }
        }
    }

    override fun onViewActive() {
        super.onViewActive()
        viewModelScope.launch {
            /**
             * Необходимо дождаться окончания запуска стартового экрана,
             * и только после этого запрашивать ПИН или сбрасывать на Главный экран.
             */
            updateState { it.copy(viewActiveConsumed = true) }
        }
    }

    override fun onViewInactive() {
        super.onViewInactive()
        updateState {
            it.copy(
                viewActiveConsumed = false,
            )
        }
    }

    private fun handleDeepLink() {
        viewModelScope.launch {

            if (deepLinkResult == null) return@launch

            viewState.first {
                it.viewActiveConsumed
            }

            val deepLinkResult = ::deepLinkResult.getAndSet(null) ?: return@launch

            deepLinkNavigator.navigateToDeepLink(deepLinkResult)
        }
    }

    companion object {
        val REQUEST_PARAMS = RequestParams(
            requestKey = "MainActivityViewModel_requestKey",
            resultKey = ContractRouter.IGNORE_RESULT,
        )

        object GuestModeRootNavigationContract :
            NavigationContract<NoParams, NoResult>(Fragment::class)
    }
}