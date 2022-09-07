/**
 * Created by Dmitry Popov on 05.09.2022.
 */
package io.fasthome.fenestram_messenger.push_impl.di

import io.fasthome.fenestram_messenger.data.StorageQualifier
import org.koin.dsl.module
import io.fasthome.fenestram_messenger.push_api.PushFeature
import io.fasthome.fenestram_messenger.push_impl.PushFeatureImpl
import io.fasthome.fenestram_messenger.push_impl.data.service.PushService
import io.fasthome.fenestram_messenger.push_impl.data.storage.PushesStorage
import io.fasthome.fenestram_messenger.push_impl.data.repo_impl.PushRepoImpl
import io.fasthome.fenestram_messenger.push_impl.domain.repo.PushRepo
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.push_impl.data.storage.RemoteMessagesStorage
import io.fasthome.network.di.singleAuthorizedService
import org.koin.core.qualifier.named

object PushModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::PushFeatureImpl) bindSafe PushFeature::class
    }

    private fun createDataModule() = module {
        factory(::PushRepoImpl) bindSafe PushRepo::class
        singleAuthorizedService(::PushService)

        single(::PushesStorage)
        single { RemoteMessagesStorage(storageFactory = get(qualifier = named(StorageQualifier.Simple))) }

    }

    private fun createDomainModule() = module {
    }

    private fun createPresentationModule() = module {
    }
}