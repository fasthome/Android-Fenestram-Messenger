package io.fasthome.fenestram_messenger.profile_impl.di

import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.profile_impl.ProfileFeatureImpl
import io.fasthome.fenestram_messenger.profile_api.ProfileFeature
import io.fasthome.fenestram_messenger.profile_impl.presentation.profile.ProfileViewModel
import org.koin.dsl.module

object ProfileModule  {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::ProfileFeatureImpl) bindSafe ProfileFeature::class
    }

    private fun createDataModule() = module {

    }

    private fun createDomainModule() = module {

    }

    private fun createPresentationModule() = module {
        viewModel(::ProfileViewModel)
    }
}