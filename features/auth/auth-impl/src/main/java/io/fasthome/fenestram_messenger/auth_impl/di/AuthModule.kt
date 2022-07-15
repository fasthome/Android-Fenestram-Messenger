/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.di

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_impl.AuthFeatureImpl
import io.fasthome.fenestram_messenger.auth_impl.domain.repo.AuthRepo
import io.fasthome.fenestram_messenger.auth_impl.data.service.AuthService
import io.fasthome.fenestram_messenger.auth_impl.data.repo_impl.AuthRepoImpl
import io.fasthome.fenestram_messenger.auth_impl.data.repo_impl.ProfileRepoImpl
import io.fasthome.fenestram_messenger.auth_impl.data.service.ProfileService
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.ProfileInteractor
import io.fasthome.fenestram_messenger.auth_impl.domain.repo.ProfileRepo
import io.fasthome.fenestram_messenger.auth_impl.presentation.welcome.WelcomeViewModel
import io.fasthome.fenestram_messenger.auth_impl.presentation.code.CodeViewModel
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.PersonalityViewModel
import io.fasthome.fenestram_messenger.auth_impl.presentation.logout.LogoutManager
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.network.client.ForceLogoutManager
import io.fasthome.network.di.NetworkClientFactoryQualifier
import org.koin.core.qualifier.named
import org.koin.dsl.bind

import org.koin.dsl.module

object AuthModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::AuthFeatureImpl) bindSafe AuthFeature::class
    }

    private fun createDataModule() = module {
        single(::AuthRepoImpl) bindSafe AuthRepo::class
        single(::ProfileRepoImpl) bindSafe ProfileRepo::class

        single { ProfileService(get(named(NetworkClientFactoryQualifier.Authorized))) }
        single { AuthService(get(named(NetworkClientFactoryQualifier.Unauthorized)), get()) }
    }

    private fun createDomainModule() = module {
        factory(::AuthInteractor)
        factory(::ProfileInteractor)
    }

    private fun createPresentationModule() = module {
        single(::LogoutManager) bindSafe ForceLogoutManager::class

        viewModel(::WelcomeViewModel)
        viewModel(::CodeViewModel)
        viewModel(::PersonalityViewModel)
    }

}