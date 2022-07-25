package io.fasthome.fenestram_messenger.messenger_impl.di

import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.messenger_impl.MessengerFeatureImpl
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.MessengerViewModel
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object MessengerModule  {
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

    }

    private fun createDomainModule() = module {

    }

    private fun createPresentationModule() = module {

        viewModel(::MessengerViewModel)
        viewModel(::ConversationViewModel)
    }
}