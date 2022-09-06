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
    }

    private fun createDomainModule() = module {
    }

    private fun createPresentationModule() = module {
        viewModel(::GroupGuestViewModel)
        viewModel(::GroupParticipantsViewModel)
    }
}