package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.graphics.Bitmap
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.component.pick_file.PickFileInterface
import io.fasthome.component.pick_file.ProfileImageUtil
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.data.ProfileImageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.MessagesPage
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.AttachedFile
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.SentStatus
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.imageViewer.ImageViewerContract
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.Message
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper.Companion.PAGE_SIZE
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.kotlin.switchJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.util.*

class ConversationViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ConversationNavigationContract.Params,
    private val features: Features,
    private val messengerInteractor: MessengerInteractor,
    private val pickFileInterface: PickFileInterface,
    private val profileImageUtil: ProfileImageUtil,
    private val profileImageUrlConverter: ProfileImageUrlConverter,
) : BaseViewModel<ConversationState, ConversationEvent>(router, requestParams) {

    private val imageViewerLauncher = registerScreen(ImageViewerContract)
    private var chatId = params.chat.id
    private var chatUsers = listOf<User>()
    private var selfUserId: Long? = null
    private var loadItemsJob by switchJob()
    private var lastPage: MessagesPage? = null

    init {
        pickFileInterface.resultEvents()
            .onEach {
                when (it) {
                    PickFileInterface.ResultEvent.PickCancelled -> Unit
                    is PickFileInterface.ResultEvent.Picked -> {
                        val bitmap = profileImageUtil.getPhoto(it.tempFile)
                        if (bitmap != null) {
                            updateState { state ->
                                state.copy(
                                    attachedFiles = state.attachedFiles.plus(
                                        AttachedFile.Image(
                                            bitmap = bitmap,
                                            file = it.tempFile
                                        )
                                    )
                                )
                            }
                        } else updateState { state ->
                            state.copy(
                                attachedFiles = state.attachedFiles.plus(
                                    AttachedFile.Document(
                                        file = it.tempFile
                                    )
                                )
                            )
                        }


                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun previousScreen(): Boolean {
        return params.fromContacts
    }

    fun loadItems() {
        loadItemsJob = viewModelScope.launch {
            lastPage?.let {
                if (it.total <= PAGE_SIZE) {
                    return@launch
                }
            }

            messengerInteractor.getChatPageItems(chatId ?: return@launch).onSuccess {
                lastPage = it
                updateState { state ->
                    state.copy(
                        messages = state.messages.plus(
                            it.messages.toConversationItems(
                                selfUserId = selfUserId!!,
                                isGroup = params.chat.isGroup,
                                profileImageUrlConverter = profileImageUrlConverter
                            )
                        )
                    )
                }
            }
        }
    }

    fun fetchMessages() {
        viewModelScope.launch {
            selfUserId = features.authFeature.getUserId(needLogout = true).getOrNull()
            if (selfUserId == null) {
                features.authFeature.logout()
                return@launch
            }

            if (params.chat.id == null) {
                messengerInteractor.postChats(
                    name = params.chat.name,
                    users = params.chat.users,
                    isGroup = params.chat.isGroup
                ).withErrorHandled {
                    chatId = it.chatId
                    if (params.chat.avatar != null && params.chat.isGroup)
                        if (messengerInteractor.patchChatAvatar(it.chatId, params.chat.avatar)
                                .successOrSendError() != null
                        )
                            updateState { state ->
                                state.copy(avatar = profileImageUrlConverter.convert(params.chat.avatar))
                            }
                }
            }
            subscribeMessages(
                chatId ?: params.chat.id ?: return@launch,
                selfUserId ?: return@launch
            )
        }
    }

    class Features(
        val profileGuestFeature: ProfileGuestFeature,
        val authFeature: AuthFeature
    )

    private val profileGuestLauncher =
        registerScreen(features.profileGuestFeature.profileGuestNavigationContract) { result ->
            when (result) {
                is ProfileGuestFeature.ProfileGuestResult.ChatDeleted ->
                    exitWithResult(
                        ConversationNavigationContract.createResult(
                            ConversationNavigationContract.Result.ChatDeleted(result.id)
                        )
                    )
            }
        }

    fun exitToMessenger() = exitWithoutResult()

    override fun createInitialState(): ConversationState {
        return ConversationState(
            messages = mapOf(),
            userName = PrintableText.Raw(params.chat.name),
            userOnline = false,
            isChatEmpty = false,
            avatar = params.chat.avatar ?: "",
            attachedFiles = listOf()
        )
    }

    fun addMessageToConversation(mess: String) {
        val attachedFiles = currentViewState.attachedFiles

        if (attachedFiles.isNotEmpty()) {
            sendImages(attachedFiles)
        }

        if (mess.isNotEmpty()) {
            sendMessage(mess)
        }
    }

    private fun sendImages(attachedFiles: List<AttachedFile>) {
        val messages = currentViewState.messages

        val tempFileMessages = attachedFiles.map {
            when (it) {
                is AttachedFile.Image -> createImageMessage(null, it.bitmap, it.file)
                is AttachedFile.Document -> createDocumentMessage(null, it.file)
            }
        }

        updateState { state ->
            state.copy(attachedFiles = listOf())
        }

        viewModelScope.launch {
            updateState { state ->
                state.copy(
                    messages = tempFileMessages.reversed().associateBy({
                        it.localId
                    }, {
                        it
                    }).plus(messages)
                )
            }

            tempFileMessages.forEach { tempMessage ->
                var imageUrl: String?
                when (tempMessage) {
                    is ConversationViewItem.Self.Image -> {
                        FileOutputStream(tempMessage.file).use { output ->
                            tempMessage.bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, output)
                        }

                        messengerInteractor.uploadProfileImage(
                            tempMessage.file?.readBytes() ?: return@launch
                        )
                            .getOrNull()?.imagePath.let {
                                imageUrl = it
                                it
                            }
                        when (messengerInteractor.sendMessage(
                            id = chatId ?: return@launch,
                            text = imageUrl ?: return@launch,
                            type = "image",
                            localId = tempMessage.localId
                        )) {
                            is CallResult.Error -> {
                                updateStatus(
                                    tempMessage,
                                    SentStatus.Error,
                                    profileImageUrlConverter.convert(imageUrl)
                                )
                            }
                            is CallResult.Success -> {
                                updateStatus(
                                    tempMessage,
                                    SentStatus.Sent,
                                    profileImageUrlConverter.convert(imageUrl)
                                )
                            }
                        }
                    }
                    is ConversationViewItem.Self.Document -> {
                        messengerInteractor.uploadDocument(
                            tempMessage.file?.readBytes() ?: return@launch
                        )
                            .getOrNull()?.documentPath.let {
                                imageUrl = it
                                it
                            }
                        when (messengerInteractor.sendMessage(
                            id = chatId ?: return@launch,
                            text = imageUrl ?: return@launch,
                            type = "document",
                            localId = tempMessage.localId
                        )) {
                            is CallResult.Error -> {
                                updateStatus(
                                    tempMessage,
                                    SentStatus.Error,
                                    profileImageUrlConverter.convert(imageUrl)
                                )
                            }
                            is CallResult.Success -> {
                                updateStatus(
                                    tempMessage,
                                    SentStatus.Sent,
                                    profileImageUrlConverter.convert(imageUrl)
                                )
                            }
                        }
                    }
                }
            }

        }
        sendEvent(ConversationEvent.MessageSent)

    }

    private fun sendMessage(mess: String) {
        viewModelScope.launch {
            val tempMessage = createTextMessage(mess)
            val messages = currentViewState.messages

            updateState { state ->
                state.copy(
                    messages = mapOf(tempMessage.localId to tempMessage).plus(messages)
                )
            }

            when (messengerInteractor.sendMessage(
                id = chatId ?: return@launch,
                text = mess,
                type = "text",
                localId = tempMessage.localId
            )) {
                is CallResult.Error -> {
                    updateStatus(tempMessage, SentStatus.Error)
                }
                is CallResult.Success -> {
                    updateStatus(tempMessage, SentStatus.Sent)
                }
            }
        }
        sendEvent(ConversationEvent.MessageSent)
    }

    private fun updateStatus(
        tempMessage: ConversationViewItem.Self,
        status: SentStatus,
        imageUrl: String? = null
    ) {
        updateState { state ->
            val newMessages = state.messages.toMutableMap()
            when (tempMessage) {
                is ConversationViewItem.Self.Text -> {
                    newMessages[tempMessage.localId] = tempMessage.copy(sentStatus = status)
                }
                is ConversationViewItem.Self.Image -> {
                    newMessages[tempMessage.localId] =
                        tempMessage.copy(sentStatus = status, content = imageUrl ?: "")
                }
            }
            state.copy(
                messages = newMessages
            )
        }
    }

    fun onUserClicked(editMode: Boolean) {
        viewModelScope.launch {
            if (chatId != null)
                messengerInteractor.getChatById(chatId!!).onSuccess {
                    chatUsers = it.chatUsers
                    profileGuestLauncher.launch(
                        ProfileGuestFeature.ProfileGuestParams(
                            id = chatId,
                            userName = it.chatName,
                            userNickname = "",
                            userAvatar = it.avatar ?: "",
                            chatParticipants = chatUsers,
                            isGroup = params.chat.isGroup,
                            userPhone = "",
                            editMode = editMode
                        )
                    )
                }
        }
    }

    fun closeSocket() {
        messengerInteractor.closeSocket()
    }

    private suspend fun subscribeMessages(chatId: Long, selfUserId: Long) {
        loadItems()
        messengerInteractor.getMessagesFromChat(chatId, selfUserId)
            .flowOn(Dispatchers.Main)
            .onEach { message ->
                updateState { state ->
                    state.copy(
                        messages = mapOf(
                            UUID.randomUUID().toString() to message.toConversationViewItem(
                                selfUserId = selfUserId,
                                isGroup = params.chat.isGroup,
                                profileImageUrlConverter
                            )
                        ).plus(state.messages)
                    )
                }
                if (message.messageType == MESSAGE_TYPE_SYSTEM) {
                    messengerInteractor.getChatById(chatId).onSuccess {
                        chatUsers = it.chatUsers
                        updateState { state ->
                            state.copy(
                                avatar = it.avatar,
                                userName = PrintableText.Raw(it.chatName),
                            )
                        }
                    }
                }

                sendEvent(ConversationEvent.MessageSent)
            }
            .launchIn(viewModelScope)
        messengerInteractor.getChatById(chatId).onSuccess {
            chatUsers = it.chatUsers
        }
    }

    fun onGroupProfileClicked(item: ConversationViewItem.Group) {
        profileGuestLauncher.launch(
            ProfileGuestFeature.ProfileGuestParams(
                id = item.id,
                userName = getPrintableRawText(item.userName),
                userNickname = "",
                userAvatar = item.avatar,
                chatParticipants = listOf(),
                isGroup = false,
                userPhone = item.phone,
                editMode = false
            )
        )
    }

    fun onAttachClicked() {
        if (currentViewState.attachedFiles.size == 10) {
            showMessage(Message.PopUp(PrintableText.StringResource(R.string.messenger_max_attach)))
            return
        }
        sendEvent(ConversationEvent.ShowSelectFromDialog)
    }

    fun selectFromCamera() {
        pickFileInterface.launchCamera()
    }

    fun selectFromGallery() {
        pickFileInterface.pickFile()
    }

    fun selectAttachFile() {
        pickFileInterface.pickFile(PickFileComponentParams.MimeType.Pdf())
    }


    fun onAttachedRemoveClicked(attachedFile: AttachedFile) {
        updateState { state ->
            state.copy(
                attachedFiles = state.attachedFiles.filter {
                    it != attachedFile
                }
            )
        }
    }

    fun onOpenMenu() {
        sendEvent(ConversationEvent.OpenMenuEvent)
    }

    fun showDialog() {
        chatId?.let { sendEvent(ConversationEvent.ShowDeleteChatDialog(it)) }
    }

    fun deleteChat(id: Long) {
        viewModelScope.launch {
            if (messengerInteractor.deleteChat(id).successOrSendError() != null)
                exitWithResult(
                    ConversationNavigationContract.createResult(
                        ConversationNavigationContract.Result.ChatDeleted(id)
                    )
                )
        }
    }

    fun onSelfMessageClicked(selfViewItem: ConversationViewItem.Self) {
        when (selfViewItem.sentStatus) {
            SentStatus.Sent -> Unit
            SentStatus.Error -> {
                sendEvent(ConversationEvent.ShowErrorSentDialog(selfViewItem))
            }
            SentStatus.Read -> Unit
            SentStatus.Loading -> Unit
            SentStatus.None -> Unit
        }
    }

    fun onRetrySentClicked(selfViewItem: ConversationViewItem.Self) {
        updateState { state ->
            state.copy(
                messages = state.messages.filter { it.key != selfViewItem.localId }
            )
        }
        when (selfViewItem) {
            is ConversationViewItem.Self.Text -> {
                sendMessage(getPrintableRawText(selfViewItem.content))
            }
            is ConversationViewItem.Self.Image -> {
                sendImages(
                    listOf(
                        AttachedFile.Image(
                            selfViewItem.bitmap ?: return,
                            selfViewItem.file ?: return
                        )
                    )
                )
            }
        }
    }

    fun onCancelSentClicked(selfViewItem: ConversationViewItem.Self) {
        updateState { state ->
            state.copy(
                messages = state.messages.filter { it.key != selfViewItem.localId }
            )
        }
    }

    fun onImageClicked(url: String? = null, bitmap: Bitmap? = null) {
        imageViewerLauncher.launch(ImageViewerContract.Params(url, bitmap))
    }

    fun onDocumentClicked() {
        //TODO ОТКРЫТИЕ ДОКУМЕНТА
    }

}