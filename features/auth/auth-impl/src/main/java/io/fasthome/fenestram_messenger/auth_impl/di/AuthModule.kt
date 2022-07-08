/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.di

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_impl.AuthFeatureImpl
import io.fasthome.fenestram_messenger.auth_impl.presentation.welcome.WelcomeViewModel
import io.fasthome.fenestram_messenger.auth_impl.presentation.code.CodeViewModel
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.PersonalityViewModel
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.fenestram_messenger.di.viewModel

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

    }

    private fun createDomainModule() = module {

    }

    private fun createPresentationModule() = module {
        viewModel(::WelcomeViewModel)
        viewModel(::CodeViewModel)
        viewModel(::PersonalityViewModel)
    }

}