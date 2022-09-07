package io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import io.fasthome.fenestram_messenger.onboarding_impl.R
import io.fasthome.fenestram_messenger.onboarding_impl.databinding.FragmentOnboardingBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingFragment: BaseFragment<OnboardingState, OnboardingEvent>(R.layout.fragment_onboarding){

    private val binding by fragmentViewBinding(FragmentOnboardingBinding::bind)

    override val vm: OnboardingViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) =with(binding) {
        super.onViewCreated(view, savedInstanceState)
    }



    override fun renderState(state: OnboardingState) = with(binding){
        when(state.onboardingList){

        }
    }
    override fun handleEvent(event: OnboardingEvent) = noEventsExpected()
}