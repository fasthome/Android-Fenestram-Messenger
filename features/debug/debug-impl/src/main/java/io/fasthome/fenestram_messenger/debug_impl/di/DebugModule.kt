/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.debug_impl.di

import io.fasthome.fenestram_messenger.debug_api.DebugFeature
import io.fasthome.fenestram_messenger.debug_impl.DebugFeatureImpl
import io.fasthome.fenestram_messenger.debug_impl.presentation.debug.DebugViewModel
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.fenestram_messenger.di.viewModel
import org.koin.core.qualifier.named

import org.koin.dsl.module

object DebugModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::DebugFeatureImpl) bindSafe DebugFeature::class
    }

    private fun createDataModule() = module {

    }

    private fun createDomainModule() = module {

    }

    private fun createPresentationModule() = module {
        viewModel(::DebugViewModel)

        factory(DebugViewModel::Features)
    }

}