package io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import io.fasthome.fenestram_messenger.onboarding_impl.R
import io.fasthome.fenestram_messenger.onboarding_impl.databinding.FragmentOnboardingBinding
import io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding.adapter.OnboardingAdapter
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setPrintableText

class OnboardingFragment :
    BaseFragment<OnboardingState, OnboardingEvent>(R.layout.fragment_onboarding) {

    private val binding by fragmentViewBinding(FragmentOnboardingBinding::bind)

    override val vm: OnboardingViewModel by viewModel()

    private val onboardingAdapter = OnboardingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            viewPager2.adapter = onboardingAdapter
            (viewPager2.getChildAt(0) as RecyclerView).itemAnimator = null
            indicator.setViewPager2(viewPager2)
            skipOnboarding.setOnClickListener() {
                vm.skipOnboarding()
            }
            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position == 2) {
                        buttonOnb.setPrintableText(PrintableText.StringResource(R.string.onboarding_letsgo_button))
                        skipOnboarding.isVisible = false
                        buttonOnb.setOnClickListener() {
                            vm.skipOnboarding()
                        }
                    } else {
                        buttonOnb.setPrintableText(PrintableText.StringResource(R.string.onboarding_more_button))
                        skipOnboarding.isVisible = true
                        buttonOnb.setOnClickListener {
                            viewPager2.currentItem = position + 1
                        }
                    }
                    super.onPageSelected(position)
                }
            })
        }
    }

    override fun syncTheme(appTheme: Theme): Unit = with(binding) {
        appTheme.context = requireContext()
        layout.setBackgroundColor(appTheme.bg0Color())
        skipOnboarding.setTextColor(appTheme.buttonInactiveColor())
        indicator.dotsColor = appTheme.buttonInactiveColor()
        indicator.selectedDotColor = appTheme.buttonInactiveColor()
        vm.updatePagerTextColor(appTheme.text0Color())
    }

    override fun renderState(state: OnboardingState) = with(binding) {
        onboardingAdapter.items = state.onboardingList
    }

    override fun handleEvent(event: OnboardingEvent) = noEventsExpected()
}