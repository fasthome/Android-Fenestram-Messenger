/**
 * Created by Vladimir Rudakov on 16.02.2023.
 */
package io.fasthome.fenestram_messenger.auth_ad_impl.di

import io.fasthome.fenestram_messenger.auth_ad_api.AuthAdFeature
import io.fasthome.fenestram_messenger.auth_ad_impl.AuthAdFeatureImpl
import io.fasthome.fenestram_messenger.auth_ad_impl.presentation.login.LoginViewModel
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.viewModel
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
    }

    private fun createDomainModule() = module {
    }

    private fun createPresentationModule() = module {
        viewModel(::LoginViewModel)
    }
}