package io.fasthome.fenestram_messenger.contacts_impl.di

import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.contacts_impl.ContactsFeatureImpl
import io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact.ContactAddViewModel
import io.fasthome.fenestram_messenger.contacts_impl.data.ContactsLoader
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.ContactsViewModel
import io.fasthome.fenestram_messenger.contacts_impl.data.repo_impl.ContactsRepoImpl
import io.fasthome.fenestram_messenger.contacts_impl.data.service.ContactsService
import io.fasthome.fenestram_messenger.contacts_impl.data.service.mapper.ContactsMapper
import io.fasthome.fenestram_messenger.contacts_impl.domain.repo.ContactsRepo
import io.fasthome.fenestram_messenger.contacts_impl.domain.logic.ContactsInteractor
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.network.di.singleAuthorizedService
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
        factory(::ContactsRepoImpl) bindSafe ContactsRepo::class
        singleAuthorizedService(::ContactsService)
        single(::ContactsMapper)
    }

    private fun createDomainModule() = module {
        factory(::ContactsInteractor)
    }

    private fun createPresentationModule() = module {
        viewModel(::ContactsViewModel)
        viewModel(::ContactAddViewModel)

        single(::ContactsLoader)
    }
}