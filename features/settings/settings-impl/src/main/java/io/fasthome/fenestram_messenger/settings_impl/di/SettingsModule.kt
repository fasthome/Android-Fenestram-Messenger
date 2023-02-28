/**
 * Created by Dmitry Popov on 20.07.2022.
 */
package io.fasthome.fenestram_messenger.settings_impl.di

import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.settings_api.SettingsFeature
import io.fasthome.fenestram_messenger.settings_impl.SettingsFeatureImpl
import io.fasthome.fenestram_messenger.settings_impl.data.repo_impl.SettingsRepoImpl
import io.fasthome.fenestram_messenger.settings_impl.data.service.SettingsService
import io.fasthome.fenestram_messenger.settings_impl.domain.repo.SettingsRepo
import io.fasthome.fenestram_messenger.settings_impl.presentation.infoapp.InfoappViewModel
import io.fasthome.fenestram_messenger.settings_impl.presentation.settings.SettingsViewModel
import io.fasthome.network.di.singleAuthorizedService
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
        singleAuthorizedService(::SettingsService)
        single(::SettingsRepoImpl) bindSafe SettingsRepo::class
    }

    private fun createDomainModule() = module {

    }

    private fun createPresentationModule() = module {
        viewModel (::SettingsViewModel)
        viewModel (::InfoappViewModel)

        factory(SettingsViewModel::Features)
    }
}

