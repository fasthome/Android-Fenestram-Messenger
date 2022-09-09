/**
 * Created by Vladimir Rudakov on 21.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_guest_impl.di

import org.koin.dsl.module
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.profile_guest_impl.ProfileGuestFeatureImpl
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.ProfileGuestViewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files.ProfileGuestFilesViewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.ProfileGuestImagesViewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.logic.DeleteChatUseCase
import io.fasthome.fenestram_messenger.profile_guest_impl.data.repo_impl.ProfileGuestImpl
import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.ProfileGuestService
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.repo.ProfileGuestRepo
import io.fasthome.network.di.NetworkClientFactoryQualifier
import org.koin.core.qualifier.named

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
        single(::ProfileGuestImpl) bindSafe ProfileGuestRepo::class
        single { ProfileGuestService(get(named(NetworkClientFactoryQualifier.Authorized))) }
    }

    private fun createDomainModule() = module {
        factory(::DeleteChatUseCase)
    }

    private fun createPresentationModule() = module {
        viewModel(::ProfileGuestViewModel)
        viewModel(::ProfileGuestFilesViewModel)
        viewModel(::ProfileGuestImagesViewModel)
        factory(ProfileGuestViewModel::Features)
    }
}