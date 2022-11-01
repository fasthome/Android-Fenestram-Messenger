package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import androidx.lifecycle.viewModelScope
import io.fasthome.component.camera.CameraComponentParams
import io.fasthome.component.imageViewer.ImageViewerContract
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.component.person_detail.PersonDetail
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.component.pick_file.PickFileInterface
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.camera_api.CameraFeature
import io.fasthome.fenestram_messenger.camera_api.CameraParams
import io.fasthome.fenestram_messenger.camera_api.ConfirmParams
import io.fasthome.fenestram_messenger.camera_api.ConfirmResult
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.ChatsMapper.Companion.TYPING_MESSAGE_STATUS
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.MessageStatus
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.MessagesPage
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.UserStatus
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.CopyDocumentToDownloadsUseCase
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.AttachedFile
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.CapturedItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.SentStatus
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.Message
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.presentation.base.navigation.OpenFileNavigationContract
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper.Companion.PAGE_SIZE
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.kotlin.switchJob
import io.fasthome.fenestram_messenger.util.model.Bytes
import io.fasthome.fenestram_messenger.util.model.Bytes.Companion.BYTES_PER_MB
import io.fasthome.network.client.ProgressListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class ConversationViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ConversationNavigationContract.Params,
    private val features: Features,
    private val messengerInteractor: MessengerInteractor,
    private val pickFileInterface: PickFileInterface,
    private val storageUrlConverter: StorageUrlConverter,
    private val copyDocumentToDownloadsUseCase: CopyDocumentToDownloadsUseCase,
    private val permissionInterface: PermissionInterface,
) : BaseViewModel<ConversationState, ConversationEvent>(router, requestParams) {

    private val imageViewerLauncher = registerScreen(ImageViewerContract)

    private val cameraLauncher =
        registerScreen(features.cameraFeature.cameraNavigationContract) { result ->
            val tempFile = result.tempFile

            photoPreviewLauncher.launch(
                ConfirmParams(
                    content = Content.FileContent(tempFile),
                )
            )
        }

    private val photoPreviewLauncher =
        registerScreen(features.cameraFeature.confirmNavigationContract) { result ->
            when (val action = result.action) {
                is ConfirmResult.Action.Confirm -> saveFile(action.tempFile)
                ConfirmResult.Action.Retake -> openCameraFragment()
                ConfirmResult.Action.Cancel -> Unit
            }
        }

    private var chatId = params.chat.id
    private var chatUsers = listOf<User>()
    private var selfUserId: Long? = null
    private var loadItemsJob by switchJob()
    private var downloadFileJob by switchJob()
    private var lastPage: MessagesPage? = null
    var firstVisibleItemPosition: Int = -1

    private val openFileLauncher = registerScreen(OpenFileNavigationContract)

    init {
        pickFileInterface.resultEvents()
            .onEach {
                when (it) {
                    PickFileInterface.ResultEvent.PickCancelled -> Unit
                    is PickFileInterface.ResultEvent.PickedImage -> {
                        attachContentFile(Content.FileContent(it.tempFile))
                    }
                    is PickFileInterface.ResultEvent.PickedFile -> {
                        updateState { state ->
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

    fun loadItems(isResumed: Boolean) {
        loadItemsJob = viewModelScope.launch {
            val firstNewMessageCount = lastPage?.let {
                if (it.total <= PAGE_SIZE) {
                    return@launch
                }
                0
            } ?: params.chat.pendingMessages

            messengerInteractor.getChatPageItems(
                isResumed,
                firstNewMessageCount.toInt(),
                chatId ?: return@launch
            ).onSuccess {
                lastPage = it

                updateState { state ->
                    state.copy(
                        messages = state.messages.plus(
                            it.messages.toConversationItems(
                                selfUserId = selfUserId!!,
                                isGroup = params.chat.isGroup,
                                profileImageUrlConverter = storageUrlConverter
                            )
                        )
                    )
                }
                if (firstNewMessageCount != 0L) {
                    updateScrollPosition()
                }
            }
        }
    }

    fun fetchMessages(isResumed: Boolean) {
        viewModelScope.launch {
            selfUserId = features.authFeature.getUserId(needLogout = true).getOrNull()
            if (selfUserId == null) {
                features.authFeature.logout()
                return@launch
            }

            if (chatId == null) {
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
                                state.copy(avatar = storageUrlConverter.convert(params.chat.avatar))
                            }
                }
            }
            subscribeMessages(
                isResumed = isResumed,
                chatId ?: params.chat.id ?: return@launch,
                selfUserId ?: return@launch
            )
            subscribeMessageActions()
        }
    }

    class Features(
        val profileGuestFeature: ProfileGuestFeature,
        val authFeature: AuthFeature,
        val cameraFeature: CameraFeature,
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
            userStatus = UserStatus.Offline.toPrintableText("", params.chat.isGroup),
            userStatusDots = PrintableText.EMPTY,
            isChatEmpty = false,
            avatar = storageUrlConverter.convert(params.chat.avatar),
            attachedFiles = listOf(),
            messageToEdit = null,
            inputMessageMode = InputMessageMode.Default,
            newMessagesCount = params.chat.pendingMessages.toInt()
        )
    }

    fun editMessageMode(
        isEditMode: Boolean,
        conversationViewItem: ConversationViewItem.Self.Text? = null,
    ) {
        updateState { state ->
            state.copy(
                attachedFiles = emptyList(),
                inputMessageMode = if (isEditMode) InputMessageMode.Edit(conversationViewItem
                    ?: return@updateState state) else InputMessageMode.Default
            )
        }
    }

    fun replyMessageMode(isReplyMode: Boolean, conversationViewItem: ConversationViewItem? = null) {
        updateState { state ->
            state.copy(
                attachedFiles = emptyList(),
                inputMessageMode = if (isReplyMode) InputMessageMode.Reply(conversationViewItem
                    ?: return@updateState state) else InputMessageMode.Default)
        }
    }

    private fun replyMessage(text: String) {
        viewModelScope.launch {
            val mode = currentViewState.inputMessageMode as? InputMessageMode.Reply ?: return@launch
            val result = messengerInteractor.replyMessage(
                chatId = chatId ?: return@launch,
                messageId = mode.messageToReply.id,
                text = text,
                messageType = MESSAGE_TYPE_TEXT//mode.messageToReply.messageType ?: return@launch
            )
            when (result) {
                is CallResult.Error -> {
                    updateState { state ->
                        onError(showErrorType = ShowErrorType.Popup, throwable = result.error)
                        state.copy(
                            inputMessageMode = InputMessageMode.Default
                        )
                    }
                }
                is CallResult.Success -> {
                    updateState { state ->
                        state.copy(
                            inputMessageMode = InputMessageMode.Default
                        )
                    }
                }
            }
        }
    }

    private fun attachContentFile(content: Content) {
        updateState { state ->
            state.copy(
                attachedFiles = state.attachedFiles.plus(
                    AttachedFile.Image(
                        content = content
                    )
                )
            )
        }
    }

    fun addMessageToConversation(mess: String) {
        val attachedFiles = currentViewState.attachedFiles

        if (attachedFiles.isNotEmpty()) {
            sendFiles(attachedFiles)
        }

        if (mess.trim().isNotEmpty()) {
            when (currentViewState.inputMessageMode) {
                is InputMessageMode.Default -> {
                    sendMessage(text = mess, messageType = MessageType.Text)
                }
                is InputMessageMode.Edit -> {
                    editMessage(mess)
                }
                is InputMessageMode.Reply -> {
                    replyMessage(mess)
                }
            }
        }
    }

    private fun sendFiles(attachedFiles: List<AttachedFile>) {
        val messages = currentViewState.messages

        val tempFileMessages = attachedFiles.map {
            when (it) {
                is AttachedFile.Image -> createImageMessage(null, it.content, getPrintableRawText(currentViewState.userName))
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
                when (tempMessage) {
                    is ConversationViewItem.Self.Image -> {
                        sendImage(tempMessage)
                    }
                    is ConversationViewItem.Self.Document -> {
                        sendDocument(tempMessage)
                    }
                    is ConversationViewItem.Self.Text -> Unit
                }
            }
        }
        sendEvent(ConversationEvent.UpdateScrollPosition(0))
    }

    private suspend fun sendImage(tempMessage: ConversationViewItem.Self.Image) {
        var fileStoragePath: String? = null
        val byteArray = when (val content = tempMessage.loadableContent) {
            is Content.FileContent -> content.file.readBytes()
            is Content.LoadableContent -> content.load()?.array
            null -> return
        }
        messengerInteractor.uploadProfileImage(
            byteArray ?: return
        ).getOrNull()?.imagePath.let {
            fileStoragePath = it
            it
        }
        sendMessage(
            text = fileStoragePath ?: return,
            messageType = MessageType.Image,
            existMessage = tempMessage
        )
    }

    private suspend fun sendDocument(tempMessage: ConversationViewItem.Self.Document) {
        var fileStoragePath: String? = null
        messengerInteractor.uploadDocument(
            tempMessage.file?.readBytes() ?: return,
            extension = tempMessage.file.name.let { fileName ->
                if (fileName.substring(fileName.lastIndexOf('.'), fileName.length) == ".pdf")
                    ".pdf"
                else
                    ".txt"
            }
        ).getOrNull()?.documentPath.let {
            fileStoragePath = it
            it
        }
        sendMessage(
            text = fileStoragePath ?: return,
            messageType = MessageType.Document,
            existMessage = tempMessage
        )
    }

    private fun editMessage(newText: String) {
        viewModelScope.launch {
            val messageToEdit = currentViewState.messageToEdit ?: return@launch
            val result = messengerInteractor.editMessage(
                chatId = chatId ?: return@launch,
                messageId = messageToEdit.id,
                newText = newText
            )

            when (result) {
                is CallResult.Error -> {
                    updateState { state ->
                        onError(showErrorType = ShowErrorType.Popup, throwable = result.error)
                        state.copy(
                            messageToEdit = null,
                            inputMessageMode = InputMessageMode.Default
                        )
                    }
                }
                is CallResult.Success -> {
                    val message =
                        currentViewState.messages.filter { it.value.id == messageToEdit.id }
                    val key = message.keys.firstOrNull() ?: return@launch
                    val newMessages = currentViewState.messages.mapValues {
                        if (it.key == key) messageToEdit.copy(
                            content = PrintableText.Raw(newText), isEdited = true
                        ) else it.value
                    }
                    updateState { state ->
                        state.copy(
                            messages = newMessages,
                            messageToEdit = null,
                            inputMessageMode = InputMessageMode.Default
                        )
                    }
                }
            }
        }
    }

    private fun sendMessage(
        text: String,
        messageType: MessageType,
        existMessage: ConversationViewItem.Self? = null,
    ) {
        viewModelScope.launch {
            val tempMessage = when (messageType) {
                MessageType.Text -> {
                    createTextMessage(text, getPrintableRawText(currentViewState.userName))
                }
                MessageType.Image -> {
                    if ((existMessage as ConversationViewItem.Self.Image).loadableContent == null) return@launch
                    existMessage
                }
                MessageType.Document -> {
                    if ((existMessage as ConversationViewItem.Self.Document).file == null) return@launch
                    existMessage
                }
            }
            val messages = currentViewState.messages

            updateState { state ->
                state.copy(
                    messages = mapOf(tempMessage.localId to tempMessage).plus(messages)
                )
            }
            val sendMessageResponse = messengerInteractor.sendMessage(
                id = chatId ?: return@launch,
                text = text,
                type = messageType.type,
                localId = tempMessage.localId,
                authorId = selfUserId ?: return@launch
            )
            when (sendMessageResponse) {
                is CallResult.Error -> {
                    updateStatus(tempMessage, SentStatus.Error)
                }
                is CallResult.Success -> {
                    when (messageType) {
                        MessageType.Text -> {
                            updateStatus(
                                tempMessage,
                                SentStatus.Sent,
                                id = sendMessageResponse.data.id
                            )
                        }
                        MessageType.Image, MessageType.Document -> {
                            updateStatus(
                                tempMessage,
                                SentStatus.Sent,
                                storageUrlConverter.convert(text),
                                sendMessageResponse.data.id
                            )
                        }
                    }

                }
            }
        }

        sendEvent(ConversationEvent.UpdateScrollPosition(0))
    }

    private fun updateStatus(
        tempMessage: ConversationViewItem.Self,
        status: SentStatus,
        imageUrl: String? = null,
        id: Long? = null,
    ) {
        updateState { state ->
            val newMessages = state.messages.toMutableMap()
            when (tempMessage) {
                is ConversationViewItem.Self.Text -> {
                    newMessages[tempMessage.localId] =
                        if (id != null) {
                            tempMessage.copy(sentStatus = status, id = id)
                        } else {
                            tempMessage.copy(sentStatus = status)
                        }
                }
                is ConversationViewItem.Self.Image -> {
                    newMessages[tempMessage.localId] =
                        if (id != null) {
                            tempMessage.copy(id = id, sentStatus = status, content = imageUrl ?: "")
                        } else {
                            tempMessage.copy(sentStatus = status, content = imageUrl ?: "")
                        }
                }
                is ConversationViewItem.Self.Document -> {
                    if (id != null) {
                        newMessages[tempMessage.localId] =
                            tempMessage.copy(sentStatus = status, content = imageUrl ?: "", id = id)
                    } else {
                        newMessages[tempMessage.localId] =
                            tempMessage.copy(sentStatus = status, content = imageUrl ?: "")
                    }
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
                messengerInteractor.getChatById(chatId!!).onSuccess { chat ->
                    chatUsers = chat.chatUsers
                    val selfUserId = messengerInteractor.getUserId()
                    profileGuestLauncher.launch(
                        ProfileGuestFeature.ProfileGuestParams(
                            id = chatId,
                            userName = chat.chatName,
                            userNickname = chat.chatUsers.firstOrNull { it.id != selfUserId }?.nickname
                                ?: "",
                            userAvatar = chat.avatar,
                            chatParticipants = chatUsers,
                            isGroup = params.chat.isGroup,
                            userPhone = chat.chatUsers.firstOrNull { it.id != selfUserId }?.phone
                                ?: "",
                            editMode = editMode && params.chat.isGroup
                        )
                    )
                }
        }
    }

    fun closeSocket() {
        messengerInteractor.closeSocket()
    }

    private suspend fun subscribeMessages(isResumed: Boolean, chatId: Long, selfUserId: Long) {
        loadItems(isResumed)
        messengerInteractor.getMessagesFromChat(
            chatId,
            selfUserId,
            { onNewMessageStatus(it) },
            { onNewPendingMessagesCallback(it) },
            { onMessagesDeletedCallback(it) }
        )
            .flowOn(Dispatchers.Main)
            .onEach { message ->
                loadItemsJob?.join()
                updateState { state ->
                    if (message.isEdited && state.messages.filter { it.value.id == message.id }
                            .isNotEmpty()) {
                        return@updateState state.copy(
                            messages = state.messages.mapValues {
                                if (it.value.id == message.id)
                                    message.toConversationViewItem(
                                        selfUserId,
                                        params.chat.isGroup,
                                        storageUrlConverter
                                    )
                                else it.value
                            }
                        )
                    }
                    state.copy(
                        messages = mapOf(
                            UUID.randomUUID().toString() to message.toConversationViewItem(
                                selfUserId = selfUserId,
                                isGroup = params.chat.isGroup,
                                storageUrlConverter
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
                if (firstVisibleItemPosition < 2) {
                    messengerInteractor.emitMessageRead(chatId, listOf(message.id))
                    sendEvent(ConversationEvent.UpdateScrollPosition(0))
                } else if (selfUserId != message.userSenderId) {
                    updateState { state -> state.copy(newMessagesCount = state.newMessagesCount + 1) }
                }
            }
            .launchIn(viewModelScope)
        messengerInteractor.getChatById(chatId).onSuccess {
            chatUsers = it.chatUsers
        }
    }

    private fun subscribeMessageActions() {
        messengerInteractor.messageActionsFlow
            .flowOn(Dispatchers.Main)
            .onEach { messageAction ->
                if (chatId == messageAction.chatId) {
                    updateState { state ->
                        state.copy(
                            userStatus = messageAction.userStatus.toPrintableText(
                                messageAction.userName,
                                params.chat.isGroup
                            ),
                            userStatusDots = PrintableText.Raw(".")
                        )
                    }
                    repeat(7) {
                        delay(300)
                        updateState { state ->
                            val userStatusDots = getPrintableRawText(state.userStatusDots)
                            val newDotCount = userStatusDots.count() % 3 + 1
                            state.copy(
                                userStatusDots = PrintableText.Raw(".".repeat(newDotCount))
                            )
                        }
                    }
                    updateState { state ->
                        state.copy(
                            userStatus = UserStatus.Online.toPrintableText(
                                messageAction.userName,
                                params.chat.isGroup
                            ),
                            userStatusDots = PrintableText.EMPTY
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onNewMessageStatus(messageStatus: MessageStatus) {
        updateState { state ->
            state.copy(
                messages = state.messages.mapValues {
                    if (it.value.id == messageStatus.messageId)
                        messageStatus.toConversationViewItem(it.value)
                    else it.value
                }
            )
        }
    }

    private fun onMessagesDeletedCallback(deletedMessages: List<Long>) {
        updateState {
            val messages: MutableMap<String, ConversationViewItem> = mutableMapOf()
            currentViewState.messages.entries.forEach { item ->
                if (!deletedMessages.contains(item.value.id))
                    messages[item.key] = item.value
            }
            it.copy(messages = messages)
        }
    }

    private fun onNewPendingMessagesCallback(pendingMessages: Int) {
        chatId?.let {
            if (firstVisibleItemPosition == 0 && pendingMessages > 0) {
                val unreadMessages = currentViewState.messages.values.toList().subList(
                    0,
                    currentViewState.messages.size
                ).map { message -> message.id }
                messengerInteractor.emitMessageRead(it, unreadMessages)
            }
        }
    }

    fun onGroupProfileClicked(item: ConversationViewItem.Group) {
        sendEvent(
            ConversationEvent.ShowPersonDetailDialog(
                PersonDetail(
                    userId = item.userId,
                    avatar = item.avatar,
                    userName = getPrintableRawText(item.userName),
                    phone = item.phone,
                    userNickname = item.nickname ?: ""
                )
            )
        )
    }

    fun onLaunchConversationClicked(personDetail: PersonDetail) {
        registerScreen(ConversationNavigationContract) {}.launch(
            ConversationNavigationContract.Params(
                chat = Chat(
                    id = null,
                    users = listOf(personDetail.userId),
                    messages = listOf(),
                    time = null,
                    name = personDetail.userName,
                    avatar = storageUrlConverter.extractPath(personDetail.avatar),
                    isGroup = false,
                    pendingMessages = 0
                )
            )
        )
    }

    private val fileCapturedItem: CapturedItem
        get() = CapturedItem(UUID.randomUUID().toString())

    fun onAttachClicked() {
        if (currentViewState.attachedFiles.size == 10) {
            showMessage(Message.PopUp(PrintableText.StringResource(R.string.messenger_max_attach)))
            return
        }
        sendEvent(ConversationEvent.ShowSelectFromDialog)
    }

    private fun openCameraFragment() {
        cameraLauncher.launch(
            CameraParams(
                cameraComponentParams = CameraComponentParams(
                    maxPhotoSize = Bytes(BYTES_PER_MB),
                    fixedOrientation = null,
                    copyToExternalDir = true
                ),
            )
        )
    }

    private fun saveFile(tempFile: File) {
        viewModelScope.launch {

            val content = fileCapturedItem.id.let {
                messengerInteractor.saveFile(it, tempFile)

                PhotoLoadableContent(it)
            }

            attachContentFile(content)
        }
    }

    fun selectFromCamera() {
        openCameraFragment()
    }

    fun selectFromGallery() {
        pickFileInterface.pickFile()
    }

    fun selectAttachFile() {
        pickFileInterface.pickFile(PickFileComponentParams.MimeType.Document())
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
                sendMessage(
                    getPrintableRawText(selfViewItem.content),
                    messageType = MessageType.Text
                )
            }
            is ConversationViewItem.Self.Image -> {
                sendFiles(
                    listOf(
                        AttachedFile.Image(
                            selfViewItem.loadableContent ?: return
                        )
                    )
                )
            }
            is ConversationViewItem.Self.Document -> {
                sendFiles(
                    listOf(AttachedFile.Document(selfViewItem.file ?: return))
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

    fun onSelfMessageLongClicked(conversationViewItem: ConversationViewItem.Self.Text) {
        sendEvent(ConversationEvent.ShowSelfMessageActionDialog(conversationViewItem))
    }

    fun onDeleteMessageClicked(conversationViewItem: ConversationViewItem.Self) {
        viewModelScope.launch {
            messengerInteractor.deleteMessage(
                messageId = conversationViewItem.id,
                chatId = chatId ?: return@launch
            ).onSuccess {
                updateState {
                    val messages: MutableMap<String, ConversationViewItem> = mutableMapOf()
                    currentViewState.messages.entries.forEach { item ->
                        if (item.value.id != conversationViewItem.id)
                            messages[item.key] = item.value
                    }
                    it.copy(messages = messages)
                }
            }

        }
    }

    fun onReceiveMessageLongClicked(conversationViewItem: ConversationViewItem.Receive.Text) {
        sendEvent(ConversationEvent.ShowReceiveMessageActionDialog(conversationViewItem))
    }

    fun onGroupMessageLongClicked(conversationViewItem: ConversationViewItem.Group.Text) {
        sendEvent(ConversationEvent.ShowGroupMessageActionDialog(conversationViewItem))
    }

    fun onSelfImageLongClicked(conversationViewItem: ConversationViewItem.Self.Image) {
        sendEvent(ConversationEvent.ShowSelfImageActionDialog(conversationViewItem))
    }

    fun onTypingMessage() {
        messengerInteractor.emitMessageAction(chatId.toString(), TYPING_MESSAGE_STATUS)
    }

    fun onDownloadDocument(
        itemSelf: ConversationViewItem.Self.Document? = null,
        itemReceive: ConversationViewItem.Receive.Document? = null,
        itemGroup: ConversationViewItem.Group.Document? = null,
        progressListener: ProgressListener,
    ) {
        val item = itemSelf ?: itemReceive ?: itemGroup ?: return
        val documentLink = item.content.toString()
        downloadFileJob = viewModelScope.launch {
            messengerInteractor.getDocument(
                storagePath = storageUrlConverter.extractPath(documentLink),
                progressListener = progressListener
            ).onSuccess { loadedDocument ->
                val permissionGranted = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                        || permissionInterface.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                if (!permissionGranted) return@launch
                copyDocumentToDownloadsUseCase.invoke(
                    loadedDocument.byteArray,
                    PrintableText.Raw(storageUrlConverter.extractPath(documentLink)),
                    extension = documentLink.substringAfterLast('.', "")
                ).onSuccess { uri ->
                    openFileLauncher.launch(OpenFileNavigationContract.Params(uri))
                }
            }
        }
    }

    fun updateScrollPosition() {
        sendEvent(ConversationEvent.UpdateScrollPosition(currentViewState.newMessagesCount))
    }

    fun onScrolledToLastPendingMessage() {
        if (firstVisibleItemPosition > currentViewState.newMessagesCount || currentViewState.newMessagesCount == 0) {
            return
        }
        chatId?.let {
            val selfMessageOffset =
                if (currentViewState.messages.toList().first().second.id == 0L) 1 else 0
            val readMessages = currentViewState.messages
                .toList().subList(
                    firstVisibleItemPosition + selfMessageOffset,
                    currentViewState.newMessagesCount + selfMessageOffset
                )
                .map { messageViewItem -> messageViewItem.second.id }
            messengerInteractor.emitMessageRead(it, readMessages)
            updateState { state -> state.copy(newMessagesCount = firstVisibleItemPosition) }
        }
    }
}