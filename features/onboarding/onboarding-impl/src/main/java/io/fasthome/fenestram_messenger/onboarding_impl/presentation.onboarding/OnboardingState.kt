package io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding

import androidx.annotation.DrawableRes
import io.fasthome.fenestram_messenger.util.PrintableText

data class OnboardingState(
    val onboardingList: List<Page>
)

data class Page(
    @DrawableRes val image: Int,
    val text: PrintableText
)