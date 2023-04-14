package io.fasthome.fenestram_messenger.contacts_impl.di

import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.contacts_impl.ContactsFeatureImpl
import io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact.ContactAddViewModel
import io.fasthome.fenestram_messenger.contacts_impl.data.ContactsLoader
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.ContactsViewModel
import io.fasthome.fenestram_messenger.contacts_impl.data.repo_impl.DepartmentRepoImpl
import io.fasthome.fenestram_messenger.contacts_impl.data.service.DepartmentService
import io.fasthome.fenestram_messenger.contacts_impl.data.service.mapper.DepartmentsMapper
import io.fasthome.fenestram_messenger.contacts_impl.domain.repo.DepartmentRepo
import io.fasthome.fenestram_messenger.contacts_impl.domain.logic.DepartmentInteractor
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
        factory(::DepartmentRepoImpl) bindSafe DepartmentRepo::class
        singleAuthorizedService(::DepartmentService)
        single(::DepartmentsMapper)
    }

    private fun createDomainModule() = module {
        factory(::DepartmentInteractor)
    }

    private fun createPresentationModule() = module {
        viewModel(::ContactsViewModel)
        viewModel(::ContactAddViewModel)

        single(::ContactsLoader)
    }
}