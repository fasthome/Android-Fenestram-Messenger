package io.fasthome.fenestram_messenger.messenger_impl.di

import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.messenger_impl.MessengerFeatureImpl
import io.fasthome.fenestram_messenger.messenger_impl.data.MessengerSocket
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.MessengerViewModel
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationViewModel
import io.fasthome.fenestram_messenger.messenger_impl.data.repo_impl.MessengerImpl
import io.fasthome.fenestram_messenger.messenger_impl.data.service.MessengerService
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.network.di.NetworkClientFactoryQualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module

object MessengerModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::MessengerFeatureImpl) bindSafe MessengerFeature::class
    }

    private fun createDataModule() = module {
        single(::MessengerImpl) bindSafe MessengerRepo::class

        single(::MessengerSocket)
        single { MessengerService(get(named(NetworkClientFactoryQualifier.Authorized)), get()) }
    }

    private fun createDomainModule() = module {
        factory(::MessengerInteractor)
    }

    private fun createPresentationModule() = module {

        viewModel(::MessengerViewModel)
        viewModel(::ConversationViewModel)
    }
}