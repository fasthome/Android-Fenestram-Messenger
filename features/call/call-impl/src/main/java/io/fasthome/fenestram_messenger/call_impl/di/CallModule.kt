/**
 * Created by Dmitry Popov on 10.01.2023.
 */
package io.fasthome.fenestram_messenger.call_impl.di

import io.fasthome.fenestram_messenger.call_api.CallFeature
import io.fasthome.fenestram_messenger.call_impl.CallFeatureImpl
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import org.koin.dsl.module

//TODO: add to AppModule.
object CallModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::CallFeatureImpl) bindSafe CallFeature::class
    }

    private fun createDataModule() = module {
    }

    private fun createDomainModule() = module {
    }

    private fun createPresentationModule() = module {
    }
}