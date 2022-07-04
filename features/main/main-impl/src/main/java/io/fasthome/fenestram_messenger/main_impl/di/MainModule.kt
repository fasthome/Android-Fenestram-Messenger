/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.main_impl.di

import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.main_api.MainFeature
import io.fasthome.fenestram_messenger.main_impl.presentation.main.MainViewModel
import io.fasthome.fenestram_messenger.main_impl.MainFeatureImpl
import io.fasthome.fenestram_messenger.main_impl.domain.logic.OuterTabNavigator
import org.koin.dsl.factory
import org.koin.dsl.module

object MainModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::MainFeatureImpl) bindSafe MainFeature::class
    }

    private fun createDataModule() = module {

    }

    private fun createDomainModule() = module {
        single(::OuterTabNavigator)
    }

    private fun createPresentationModule() = module {
        viewModel(::MainViewModel)

        factory(MainViewModel::Features)
    }

}