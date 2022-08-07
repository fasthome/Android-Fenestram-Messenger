/**
 * Created by Vladimir Rudakov on 21.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_guest_impl.di

import org.koin.dsl.module
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.profile_guest_impl.ProfileGuestFeatureImpl
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.ProfileGuestViewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files.ProfileGuestFilesViewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.ProfileGuestImagesViewModel

object ProfileGuestModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::ProfileGuestFeatureImpl) bindSafe ProfileGuestFeature::class
    }

    private fun createDataModule() = module {
    }

    private fun createDomainModule() = module {
    }

    private fun createPresentationModule() = module {
        viewModel(::ProfileGuestViewModel)
        viewModel(::ProfileGuestFilesViewModel)
        viewModel(::ProfileGuestImagesViewModel)
    }
}