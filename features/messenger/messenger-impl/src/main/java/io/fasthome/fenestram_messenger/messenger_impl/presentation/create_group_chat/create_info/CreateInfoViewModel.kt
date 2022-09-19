/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info

import androidx.lifecycle.viewModelScope
import io.fasthome.component.pick_file.PickFileInterface
import io.fasthome.component.pick_file.ProfileImageUtil
import io.fasthome.fenestram_messenger.data.ProfileImageUrlConverter
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationEvent
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.AttachedFile
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info.model.AvatarImage
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.mapper.mapToContactViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.Message
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getOrNull
import io.fasthome.fenestram_messenger.util.getPrintableRawText
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CreateInfoViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: CreateInfoContract.Params,
    private val messengerInteractor: MessengerInteractor,
    private val pickFileInterface: PickFileInterface,
    private val profileImageUtil: ProfileImageUtil
) : BaseViewModel<CreateInfoState, CreateInfoEvent>(router, requestParams) {

    private val conversationLauncher = registerScreen(ConversationNavigationContract) { }

    init {
        pickFileInterface.resultEvents()
            .onEach {
                when (it) {
                    PickFileInterface.ResultEvent.PickCancelled -> Unit
                    is PickFileInterface.ResultEvent.Picked -> {
                        val bitmap = profileImageUtil.getPhoto(it.tempFile)
                        if (bitmap != null) {
                            updateState { state ->
                                state.copy(avatarImage = AvatarImage(bitmap, it.tempFile))
                            }
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun createInitialState(): CreateInfoState {
        return CreateInfoState(params.contacts.map(::mapToContactViewItem), null)
    }

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }

    fun onReadyClicked(chatName: String) {
        if (chatName.isEmpty()) {
            showPopup(PrintableText.StringResource(R.string.messenger_input_chat_name))
            return
        }

        viewModelScope.launch {
            val imageUrl: String? = currentViewState.avatarImage?.let { avatarImage ->
                messengerInteractor.uploadProfileImage(
                    avatarImage.file.readBytes()
                ).getOrNull()?.imagePath
            }?.apply { substring(20, length) }

            router.backTo(null)
            conversationLauncher.launch(
                ConversationNavigationContract.Params(
                    Chat(
                        null,
                        name = chatName,
                        users = params.contacts.map { it.userId ?: 0 },
                        messages = listOf(),
                        time = null,
                        avatar = imageUrl,
                        isGroup = true
                    )
                )
            )
        }
    }

    fun onAvatarClicked() {
        sendEvent(CreateInfoEvent.ShowSelectFromDialog)
    }

    fun selectFromCamera() {
        pickFileInterface.launchCamera()
    }

    fun selectFromGallery() {
        pickFileInterface.pickFile()
    }


}