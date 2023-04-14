package io.fasthome.fenestram_messenger.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import io.fasthome.fenestram_messenger.AppFeatureImpl
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.ContractRouterImpl
import io.fasthome.fenestram_messenger.navigation.CustomCicerone
import io.fasthome.fenestram_messenger.presentation.base.AppFeature
import io.fasthome.fenestram_messenger.ui.main.ActionHandler
import io.fasthome.fenestram_messenger.ui.main.MainActivityViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object PresentationModule {

    operator fun invoke(): List<Module> = listOf(
        createNavigationModule(),
        createMainActivityModule()
    )

    private fun createNavigationModule() = module {
        single { Cicerone.create(ContractRouterImpl()) }
        single { get<CustomCicerone>().getNavigatorHolder() }
        single { get<CustomCicerone>().router }
            .bindSafe(ContractRouter::class)
            .bindSafe(Router::class)
        factory(::AppFeatureImpl) bindSafe AppFeature::class
    }

    private fun createMainActivityModule() = module {
        viewModel(::MainActivityViewModel)
        factory(MainActivityViewModel::Features)

        factory(::ActionHandler)
    }
}