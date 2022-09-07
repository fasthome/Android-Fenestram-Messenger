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

    private var viewPager2: ViewPager2? = null

    private val pager2Callback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            if (position == PageLists.introSlides.size - 1) {


            } else {
                viewPager2?.currentItem = position + 1
            }
        }
    }

    override fun createInitialState(): OnboardingState {
        return OnboardingState(PageLists.introSlides)
    }

//    private fun setupViewPager()
//    {
//        val adapter = OnboardingAdapter(PageLists.introSlides)
//        viewPager2?.adapter = adapter
//        viewPager2?.registerOnPageChangeCallback()
//
//    }


}