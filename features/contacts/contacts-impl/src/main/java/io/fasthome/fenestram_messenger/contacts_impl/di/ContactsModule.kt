package io.fasthome.fenestram_messenger.contacts_impl.di

import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.contacts_impl.ContactsFeatureImpl
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.ContactsViewModel
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.viewModel
import org.koin.dsl.module

object ContactsModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::ContactsFeatureImpl) bindSafe ContactsFeature::class
    }

    private fun createDataModule() = module {

    }

    private fun createDomainModule() = module {

    }

    private fun createPresentationModule() = module {
        viewModel(::ContactsViewModel)
    }
}