/**
 * Created by Viktor Solodskiy on 17.08.2022.
 */
package io.fasthome.fenestram_messenger.onboarding_impl.di

import org.koin.dsl.module
import io.fasthome.fenestram_messenger.onboarding_api.OnboardingFeature
import io.fasthome.fenestram_messenger.onboarding_impl.OnboardingFeatureImpl
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding.OnboardingViewModel

//TODO: add to AppModule.
object OnboardingModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::OnboardingFeatureImpl) bindSafe OnboardingFeature::class
    }

    private fun createDataModule() = module {
    }

    private fun createDomainModule() = module {
    }

    private fun createPresentationModule() = module {
        viewModel(::OnboardingViewModel)
    }
}