/**
 * Created by Dmitry Popov on 30.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.di

import org.koin.dsl.module
import io.fasthome.fenestram_messenger.group_guest_api.GroupGuestFeature
import io.fasthome.fenestram_messenger.group_guest_impl.GroupGuestFeatureImpl
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.GroupGuestViewModel
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.GroupParticipantsViewModel
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.group_guest_impl.domain.logic.GroupGuestInteractor
import io.fasthome.fenestram_messenger.group_guest_impl.domain.repo.GroupGuestRepo
import io.fasthome.fenestram_messenger.group_guest_impl.data.repo_impl.GroupGuestRepoImpl
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.GroupGuestService
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.mapper.AddUsersToChatMapper
import io.fasthome.network.di.singleAuthorizedService

object GroupGuestModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::GroupGuestFeatureImpl) bindSafe GroupGuestFeature::class
    }

    private fun createDataModule() = module {
        single(::GroupGuestRepoImpl) bindSafe GroupGuestRepo::class
        singleAuthorizedService(::GroupGuestService)
        factory(::AddUsersToChatMapper)
    }

    private fun createDomainModule() = module {
        factory(::GroupGuestInteractor)
    }

    private fun createPresentationModule() = module {
        viewModel(::GroupGuestViewModel)
        viewModel(::GroupParticipantsViewModel)
    }
}