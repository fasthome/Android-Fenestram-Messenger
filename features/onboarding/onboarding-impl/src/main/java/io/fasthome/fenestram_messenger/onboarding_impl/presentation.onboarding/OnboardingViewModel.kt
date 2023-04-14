package io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.navigation.model.createResult
import io.fasthome.fenestram_messenger.onboarding_impl.R

class OnboardingViewModel(
    router: ContractRouter, requestParams: RequestParams
) : BaseViewModel<OnboardingState, OnboardingEvent>(router, requestParams) {


    override fun createInitialState(): OnboardingState {
        return OnboardingState(PageLists.getIntroSlides(R.color.text_0))
    }

    fun updatePagerTextColor(textColor: Int) {
        updateState { state ->
            state.copy(onboardingList = currentViewState.onboardingList.map {
                it.copy(textColor = textColor)
            })
        }
    }

    fun skipOnboarding() {
        exitWithResult(OnboardingNavigationContract.createResult())
    }

}