package io.fasthome.fenestram_messenger.profile_impl.di

import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.profile_impl.ProfileFeatureImpl
import io.fasthome.fenestram_messenger.profile_impl.data.repo_impl.ProfileRepoImpl
import io.fasthome.fenestram_messenger.profile_impl.data.service.ProfileService
import io.fasthome.fenestram_messenger.profile_impl.domain.repo.ProfileRepo
import io.fasthome.fenestram_messenger.profile_impl.domain.logic.ProfileInteractor
import io.fasthome.fenestram_messenger.profile_impl.data.service.mapper.ProfileMapper
import io.fasthome.fenestram_messenger.profile_api.ProfileFeature
import io.fasthome.fenestram_messenger.profile_impl.presentation.profile.ProfileViewModel
import io.fasthome.network.di.singleAuthorizedService
import org.koin.dsl.module
import io.fasthome.fenestram_messenger.di.single

object ProfileModule {
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
        single(::ProfileRepoImpl) bindSafe ProfileRepo::class

        factory(::ProfileMapper)
        singleAuthorizedService(::ProfileService)
    }

    private fun createDomainModule() = module {
        factory(::ProfileInteractor)

    }

    private fun createPresentationModule() = module {
        viewModel(::ProfileViewModel)
    }
}