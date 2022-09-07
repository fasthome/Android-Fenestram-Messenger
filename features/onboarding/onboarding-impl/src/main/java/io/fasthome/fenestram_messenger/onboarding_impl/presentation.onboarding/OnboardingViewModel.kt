package io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding

import androidx.viewpager2.widget.ViewPager2
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding.adapter.OnboardingAdapter

class OnboardingViewModel(
    router: ContractRouter,
    requestParams: RequestParams
) : BaseViewModel<OnboardingState, OnboardingEvent>(router, requestParams) {

    override fun createInitialState(): OnboardingState {
        return OnboardingState(PageLists.introSlides)
    }

    fun skipOnboarding(){
        exitWithoutResult()
    }




}