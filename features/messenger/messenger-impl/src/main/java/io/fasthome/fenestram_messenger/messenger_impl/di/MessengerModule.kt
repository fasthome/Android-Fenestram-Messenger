package io.fasthome.fenestram_messenger.messenger_impl.di

import io.fasthome.component.image_viewer.ImageViewerViewModel
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.data.StorageQualifier
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.messenger_impl.MessengerFeatureImpl
import io.fasthome.fenestram_messenger.messenger_impl.data.MessengerSocket
import io.fasthome.fenestram_messenger.messenger_impl.data.repo_impl.MessengerRepoImpl
import io.fasthome.fenestram_messenger.messenger_impl.data.service.MessengerService
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.ChatsMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.ContentMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.GetChatByIdMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.GetChatsMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.storage.ChatStorage
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.BadgeCounter
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.CopyDocumentToDownloadsUseCase
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.DownloadDocumentUseCase
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationViewModel
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info.CreateInfoViewModel
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.CreateGroupChatViewModel
import io.fasthome.component.file_selector.FileSelectorViewModel
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.MessengerViewModel
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.mapper.MessengerMapper
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper
import io.fasthome.network.di.singleAuthorizedService
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
        single(::MessengerRepoImpl) bindSafe MessengerRepo::class

        factory(::GetChatsMapper)
        factory(::GetChatByIdMapper)
        factory(::ChatsMapper)
        factory(::ContentMapper)
        single {
            ChatStorage(
                databaseFactory = get(named(StorageQualifier.Simple)),
            )
        }
        factory { MessengerSocket(get<Environment>().endpoints.baseUrl) }
        singleAuthorizedService(::MessengerService)
    }

    private fun createDomainModule() = module {
        factory(::MessengerInteractor)
        factory(::CopyDocumentToDownloadsUseCase)
        factory(::DownloadDocumentUseCase)
        single(::BadgeCounter)
    }

    private fun createPresentationModule() = module {

        viewModel(::MessengerViewModel)
        viewModel(::ConversationViewModel)
        viewModel(::CreateGroupChatViewModel)
        viewModel(::CreateInfoViewModel)
        viewModel(::ImageViewerViewModel)
        viewModel(::FileSelectorViewModel)

        factory(::PagingDataViewModelHelper)
        factory(::MessengerMapper)

        factory(ConversationViewModel::Features)
    }
}