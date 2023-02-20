/**
 * Created by Vladimir Rudakov on 16.02.2023.
 */
package io.fasthome.fenestram_messenger.auth_ad_impl.di

import io.fasthome.fenestram_messenger.auth_ad_api.AuthAdFeature
import io.fasthome.fenestram_messenger.auth_ad_impl.AuthAdFeatureImpl
import io.fasthome.fenestram_messenger.auth_ad_impl.data.repo_impl.AuthAdRepoImpl
import io.fasthome.fenestram_messenger.auth_ad_impl.data.service.AuthAdService
import io.fasthome.fenestram_messenger.auth_ad_impl.data.storage.MockLoginStorage
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.logic.AuthAdInteractor
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.repo.AuthAdRepo
import io.fasthome.fenestram_messenger.auth_ad_impl.presentation.login.LoginViewModel
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.network.di.NetworkClientFactoryQualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module

object AuthAdModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::AuthAdFeatureImpl) bindSafe AuthAdFeature::class
    }

    private fun createDataModule() = module {
        single(::AuthAdRepoImpl) bindSafe AuthAdRepo::class
        single { AuthAdService(get(named(NetworkClientFactoryQualifier.Unauthorized))) }
        single { MockLoginStorage() }
    }

    private fun createDomainModule() = module {
        factory(::AuthAdInteractor)
    }

    private fun createPresentationModule() = module {
        viewModel(::LoginViewModel)
    }
}