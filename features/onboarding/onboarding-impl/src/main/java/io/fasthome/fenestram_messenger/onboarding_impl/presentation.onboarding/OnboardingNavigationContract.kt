package io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.navigation.model.UnitResult

object OnboardingNavigationContract  : NavigationContract<NoParams, UnitResult>(OnboardingFragment::class)