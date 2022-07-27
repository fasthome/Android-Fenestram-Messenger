/**
 * Created by Dmitry Popov on 20.07.2022.
 */
package io.fasthome.fenestram_messenger.settings_impl.di

import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.settings_api.SettingsFeature
import io.fasthome.fenestram_messenger.settings_impl.SettingsFeatureImpl
import io.fasthome.fenestram_messenger.settings_impl.presentation.settings.SettingsViewModel
import org.koin.dsl.module

object SettingsModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::SettingsFeatureImpl) bindSafe SettingsFeature::class
    }

    private fun createDataModule() = module {
    }

    private fun createDomainModule() = module {
    }

    private fun createPresentationModule() = module {
        viewModel (::SettingsViewModel)


    }
}

