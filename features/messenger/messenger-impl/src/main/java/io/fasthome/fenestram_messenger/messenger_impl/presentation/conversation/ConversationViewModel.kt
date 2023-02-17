package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.Manifest
import android.os.Build
import androidx.lifecycle.viewModelScope
import io.fasthome.component.camera.CameraComponentParams
import io.fasthome.component.gallery.GalleryImage
import io.fasthome.component.image_viewer.ImageViewerContract
import io.fasthome.component.image_viewer.ImageViewerModel
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.component.person_detail.PersonDetail
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.component.pick_file.PickFileInterface
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.camera_api.*
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.messenger_api.entity.ActionMessageBlank
import io.fasthome.fenestram_messenger.messenger_api.entity.ChatChanges
import io.fasthome.fenestram_messenger.messenger_api.entity.MessageInfo
import io.fasthome.fenestram_messenger.messenger_api.entity.MessageType
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.ChatsMapper.Companion.TYPING_MESSAGE_STATUS
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.*
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.DownloadDocumentUseCase
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.AttachedFileMapper.toAttachedImages
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.AttachedFileMapper.toContentUriList
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.AttachedFileMapper.toContents
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.AttachedFileMapper.toFiles
import io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector.FileSelectorNavigationContract
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.presentation.base.navigation.OpenFileNavigationContract
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UriLoadableContent
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper.Companion.PAGE_SIZE
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.kotlin.switchJob
import io.fasthome.fenestram_messenger.util.links.USER_TAG_PATTERN
import io.fasthome.fenestram_messenger.util.links.getNicknameFromLink
import io.fasthome.fenestram_messenger.util.model.Bytes
import io.fasthome.fenestram_messenger.util.model.Bytes.Companion.BYTES_PER_MB
import io.fasthome.fenestram_messenger.util.model.MetaInfo
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
    private val downloadDocumentUseCase: DownloadDocumentUseCase,
    private val permissionInterface: PermissionInterface,
) : BaseViewModel<ConversationState, ConversationEvent>(router, requestParams) {

    companion object {
        private const val MAX_ATTACHMENTS = 10
    }

    class Features(
        val profileGuestFeature: ProfileGuestFeature,
        val authFeature: AuthFeature,
        val cameraFeature: CameraFeature,
        val messengerFeature: MessengerFeature,
    )

    private var chatId = params.chat.id
    private var chatUsers = listOf<User>()
    private var selfUserId: Long? = null
    private var loadItemsJob by switchJob()
    private var downloadFileJob by switchJob()
    private var profileOpenJob by switchJob()
    private var lastPage: MessagesPage? = null
    var firstVisibleItemPosition: Int = -1
    private var userDeleteChat: Boolean = false
    private var permittedReactions = listOf<PermittedReactionViewItem>()
    private var wasResumed: Boolean = false

    private val fileSelectorLauncher = registerScreen(FileSelectorNavigationContract) { result ->
        when (result) {
            is FileSelectorNavigationContract.Result.Attach -> {
                addContentImagesToAttach(result.images)
            }
            FileSelectorNavigationContract.Result.OpenCamera -> {
                sendEvent(ConversationEvent.OpenCamera)
            }
            FileSelectorNavigationContract.Result.OpenFiles -> {
                sendEvent(ConversationEvent.OpenFilePicker)
            }
            FileSelectorNavigationContract.Result.OpenGallery -> {
                sendEvent(ConversationEvent.OpenImagePicker)
            }
        }
    }

    private val imageViewerLauncher = registerScreen(ImageViewerContract) { result ->
        when (result) {
            is ImageViewerContract.Result.Delete -> {
                deleteMessage(result.messageId)
            }
            is ImageViewerContract.Result.Forward -> {
                openChatSelectorForForward(
                    MessageInfo(
                        id = result.messageId,
                        type = MessageType.Image
                    ), result.username
                )
            }
        }
    }

    private val messengerLauncher =
        registerScreen(features.messengerFeature.messengerNavigationContract)

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

    private val openFileLauncher = registerScreen(OpenFileNavigationContract)

    private val profileGuestLauncher =
        registerScreen(features.profileGuestFeature.profileGuestNavigationContract) { result ->
            when (result) {
                is ProfileGuestFeature.ProfileGuestResult.ChatDeleted ->
                    exitWithResult(
                        ConversationNavigationContract.createResult(
                            ConversationNavigationContract.Result.ChatDeleted(result.id)
                        )
                    )
                is ProfileGuestFeature.ProfileGuestResult.ChatNameChanged -> {
                    updateState { state -> state.copy(userName = result.newName) }
                }
                ProfileGuestFeature.ProfileGuestResult.Canceled -> {}
            }
        }

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
                                inputMessageMode = InputMessageMode.Default(
                                    attachedFiles = ((state.inputMessageMode as? InputMessageMode.Default)?.attachedFiles
                                        ?: emptyList()).plus(
                                        AttachedFile.Document(
                                            file = it.tempFile
                                        )
                                    )
                                )
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun createInitialState(): ConversationState {
        return ConversationState(
            messages = mapOf(),
            userName = PrintableText.Raw(params.chat.name),
            userStatus = UserStatus.OnlineStatus.toPrintableText(
                "",
                params.chat.isGroup,
                chatUsers
            ),
            userStatusDots = PrintableText.EMPTY,
            isChatEmpty = false,
            avatar = storageUrlConverter.convert(params.chat.avatar),
            inputMessageMode = InputMessageMode.Default(),
            newMessagesCount = params.chat.pendingMessages.toInt()
        )
    }

    fun loadPage(isResumed: Boolean) {
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
            getMessages(isResumed)

            params.forwardMessage?.let {
                updateState { state ->
                    state.copy(inputMessageMode = InputMessageMode.Forward(it))
                }
            }

            params.actionMessageBlank?.let {
                when (it) {
                    is ActionMessageBlank.Image -> {
                        if (!wasResumed) {
                            pickFileInterface.processUri(it.uri)
                            wasResumed = true
                        }
                    }
                    is ActionMessageBlank.Text -> {
                        sendEvent(ConversationEvent.ExtraText(it.text))
                    }
                }
            }
        }
    }

    private suspend fun getMessages(isResumed: Boolean) {
        selfUserId = features.authFeature.getUserId(needLogout = true).getOrNull()
        if (selfUserId == null) {
            features.authFeature.logout()
            return
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
            chatId ?: params.chat.id ?: return,
            selfUserId ?: return
        )
        subscribeMessageActions()
    }

    fun previousScreen(): Boolean {
        return params.fromContacts
    }

    fun exitToMessenger() {
        messengerInteractor.emitChatListeners(null, chatId)
        exitWithoutResult()
    }

    fun openChatSelectorForForward(messageInfo: MessageInfo, userName: PrintableText) {
        messengerLauncher.launch(
            MessengerFeature.MessengerParams(
                chatSelectionMode = true,
                forwardMessage = MessengerFeature.ForwardMessage(messageInfo, userName)
            )
        )
    }

    private fun forwardMessage() {
        viewModelScope.launch {
            messengerInteractor.forwardMessage(
                chatId ?: return@launch,
                (currentViewState.inputMessageMode as? InputMessageMode.Forward)?.messageToForward?.message?.id
                    ?: return@launch
            )
                .withErrorHandled {
                    updateState { state ->
                        state.copy(inputMessageMode = InputMessageMode.Default())
                    }
                }
        }
    }

    fun editMessageMode(
        isEditMode: Boolean,
        conversationViewItem: ConversationViewItem.Self? = null,
    ) {
        updateState { state ->
            state.copy(
                inputMessageMode = if (isEditMode) InputMessageMode.Edit(
                    conversationViewItem
                        ?: return@updateState state
                ) else InputMessageMode.Default()
            )
        }
    }

    fun replyMessageMode(isReplyMode: Boolean, conversationViewItem: ConversationViewItem? = null) {
        updateState { state ->
            state.copy(
                inputMessageMode = if (isReplyMode) InputMessageMode.Reply(
                    conversationViewItem
                        ?: return@updateState state
                ) else InputMessageMode.Default()
            )
        }
    }

    private fun replyMessage(text: String) {
        viewModelScope.launch {
            val mode = currentViewState.inputMessageMode as? InputMessageMode.Reply ?: return@launch
            val result = messengerInteractor.replyMessage(
                chatId = chatId ?: return@launch,
                messageId = mode.messageToReply.id,
                text = text,
                messageType = MESSAGE_TYPE_TEXT
            )
            when (result) {
                is CallResult.Error -> {
                    updateState { state ->
                        onError(showErrorType = ShowErrorType.Popup, throwable = result.error)
                        state.copy(
                            inputMessageMode = InputMessageMode.Default()
                        )
                    }
                }
                is CallResult.Success -> {
                    updateState { state ->
                        val tempMessage =
                            (result.data ?: return@updateState state).toConversationViewItem(
                                selfUserId,
                                params.chat.isGroup,
                                storageUrlConverter
                            ) as ConversationViewItem.Self
                        var messages = state.messages
                        messages = mapOf(tempMessage.localId to tempMessage).plus(messages)
                        state.copy(
                            inputMessageMode = InputMessageMode.Default(),
                            messages = messages
                        )
                    }
                }
            }
            sendEvent(ConversationEvent.UpdateScrollPosition(0))
        }
    }

    private fun addContentImagesToAttach(attachedContent: List<Content>) {
        updateState { state ->
            val newUriContent = attachedContent.filterIsInstance<UriLoadableContent>()
            val newAttachFiles = mutableListOf<AttachedFile>()
            newAttachFiles.addAll(attachedContent.toAttachedImages())
            ((state.inputMessageMode as? InputMessageMode.Default)?.attachedFiles
                ?: emptyList()).forEach {
                when (it) {
                    is AttachedFile.Document -> {
                        newAttachFiles.add(it)
                    }
                    is AttachedFile.Image -> {
                        if (it.content !is UriLoadableContent || newUriContent.isEmpty()) {
                            newAttachFiles.add(it)
                        }
                    }
                }
            }
            state.copy(
                inputMessageMode = InputMessageMode.Default(
                    attachedFiles = newAttachFiles
                )
            )
        }
    }

    private fun attachContentFile(content: Content) = addContentImagesToAttach(listOf(content))

    fun addMessageToConversation(mess: String) {
        val fileState = currentViewState.inputMessageMode as? InputMessageMode.Default
        fileState?.let {
            if (it.attachedFiles.isNotEmpty()) {
                sendFiles(it.attachedFiles)
            }
        }

        if (currentViewState.inputMessageMode is InputMessageMode.Forward) {
            forwardMessage()
            if (mess.trim().isNotEmpty())
                sendMessage(text = mess, messageType = MessageType.Text)
        }

        if (mess.trim().isNotEmpty()) {
            val state = currentViewState.inputMessageMode
            when (state) {
                is InputMessageMode.Default -> {
                    if (state.attachedFiles.isNotEmpty()) {
                        sendFiles(state.attachedFiles)
                    }
                    sendMessage(text = mess, messageType = MessageType.Text)
                }
                is InputMessageMode.Edit -> {
                    editMessage(mess)
                }
                is InputMessageMode.Reply -> {
                    replyMessage(mess)
                }
                is InputMessageMode.Forward -> {}
            }
        }
    }

    private fun sendFiles(attachedFiles: List<AttachedFile>) {
        var tempFileMessages: List<ConversationViewItem.Self> = emptyList()
        viewModelScope.launch {
            val allFiles = attachedFiles.filterIsInstance<AttachedFile.Document>()
            val chunkedFiles = allFiles.chunked(MAX_ATTACHMENTS)
            val allImages = attachedFiles.filterIsInstance<AttachedFile.Image>()
            val chunkedImages = allImages.chunked(MAX_ATTACHMENTS)

            chunkedFiles.forEach { attachedDocument ->
                if (attachedDocument.isNotEmpty()) {
                    val fileMessage = createDocumentMessage(emptyList(), attachedDocument.toFiles())
                    tempFileMessages = tempFileMessages + fileMessage
                    sendDocument(fileMessage)
                }
            }

            chunkedImages.forEach { images ->
                if (images.isNotEmpty()) {
                    val imageMessage = createImageMessage(
                        images.toContents(),
                        getPrintableRawText(currentViewState.userName)
                    )
                    tempFileMessages = tempFileMessages + imageMessage

                    sendImages(imageMessage)
                }
            }
            updateState { state ->
                state.copy(
                    inputMessageMode = InputMessageMode.Default(),
                    messages = tempFileMessages.reversed().associateBy({
                        it.localId
                    }, {
                        it
                    }).plus(currentViewState.messages)
                )
            }
        }
        sendEvent(ConversationEvent.UpdateScrollPosition(0))
    }

    private suspend fun sendImages(conversationViewItem: ConversationViewItem.Self.Image) {
        updateSendLoadingState(true)
        var tempMessage = conversationViewItem
        if (tempMessage.loadableContent == null) return
        var byteArrays: List<ByteArray> = emptyList()
        var filename: List<String> = emptyList()

        conversationViewItem.loadableContent?.map { content ->
            when (content) {
                is Content.FileContent -> {
                    byteArrays = byteArrays + content.file.readBytes()
                    filename = filename + content.file.name
                }
                is Content.LoadableContent -> {
                    byteArrays = byteArrays + (content.load()?.array ?: byteArrayOf())
                    filename = filename + UUID.randomUUID().toString()
                }
            }
        }

        val result = messengerInteractor.uploadImages(
            chatId = chatId ?: return,
            imagesBytes = byteArrays,
            filename = filename
        )

        when (result) {
            is CallResult.Error -> {
                updateState { state ->
                    updateStatus(tempMessage, SentStatus.Error)
                    state
                }
            }
            is CallResult.Success -> {
                tempMessage =
                    tempMessage.copy(userName = PrintableText.Raw(result.data.message?.initiator?.name ?: ""),
                        metaInfo = result.data.message?.content?.map {
                            MetaInfo(
                                name = it.name,
                                extension = it.extension,
                                size = it.size,
                                url = storageUrlConverter.convert(it.url)
                            )
                        } ?: return)
                updateStatus(
                    tempMessage,
                    SentStatus.Received,
                    result.data.message?.content?.firstOrNull()?.url,
                    result.data.message?.id
                )
                updateSendLoadingState(false)
            }
        }
        sendEvent(ConversationEvent.UpdateScrollPosition(0))
    }

    fun updateSendLoadingState(isLoading: Boolean) {
        sendEvent(ConversationEvent.SendLoading(isLoading))
    }

    private suspend fun sendDocument(conversationViewItem: ConversationViewItem.Self.Document) {
        updateSendLoadingState(true)
        var tempMessage = conversationViewItem
        val result = messengerInteractor.uploadDocuments(
            chatId ?: return,
            conversationViewItem.files?.map { it.readBytes() } ?: return,
            conversationViewItem.files.map { it.name ?: UUID.randomUUID().toString() }
        )

        when (result) {
            is CallResult.Error -> {
                updateStatus(tempMessage, SentStatus.Error)
            }
            is CallResult.Success -> {
                tempMessage =
                    tempMessage.copy(userName = conversationViewItem.userName,
                        metaInfo = result.data)
                updateStatus(
                    tempMessage,
                    SentStatus.Received,
                    result.data.firstOrNull()?.url,
                    conversationViewItem.id
                )
                updateSendLoadingState(false)
            }
        }
    }

    private fun editMessage(newText: String) {
        viewModelScope.launch {
            val messageToEdit =
                (currentViewState.inputMessageMode as? InputMessageMode.Edit
                    ?: return@launch).messageToEdit
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
                            inputMessageMode = InputMessageMode.Default()
                        )
                    }
                }
                is CallResult.Success -> {
                    val message =
                        currentViewState.messages.filter { it.value.id == messageToEdit.id }
                    val key = message.keys.firstOrNull() ?: return@launch
                    val newMessages = currentViewState.messages.mapValues {
                        if (it.key == key) {
                            when (messageToEdit) {
                                is ConversationViewItem.Self.Text -> {
                                    messageToEdit.copy(
                                        content = PrintableText.Raw(newText), isEdited = true
                                    )
                                }
                                is ConversationViewItem.Self.TextReplyOnImage -> {
                                    messageToEdit.copy(
                                        content = PrintableText.Raw(newText), isEdited = true
                                    )
                                }
                                else -> {
                                    it.value
                                }
                            }

                        } else it.value
                    }
                    updateState { state ->
                        state.copy(
                            messages = newMessages,
                            inputMessageMode = InputMessageMode.Default()
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
        messengerInteractor.emitChatListeners(chatId, null)
        viewModelScope.launch {
            val tempMessage = when (messageType) {
                MessageType.Text -> {
                    createTextMessage(text)
                }
                MessageType.Image -> {
                    if ((existMessage as ConversationViewItem.Self.Image).loadableContent == null) return@launch
                    existMessage
                }
                MessageType.Document, MessageType.Unknown -> return@launch
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
                    tempMessage.userName =
                        PrintableText.Raw(sendMessageResponse.data.userName ?: "")
                    if (messageType == MessageType.Text) {
                        updateStatus(
                            tempMessage,
                            SentStatus.Received,
                            id = sendMessageResponse.data.id
                        )
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
                is ConversationViewItem.Self.Forward -> {}
                is ConversationViewItem.Self.TextReplyOnImage -> {}
            }
            state.copy(
                messages = newMessages
            )
        }
    }

    fun onSelectUserTagClicked(user: User) {
        sendEvent(ConversationEvent.ShowUsersTags(emptyList()))
        sendEvent(ConversationEvent.UpdateInputUserTag(nickname = user.nickname))
    }

    fun onUserTagClicked(userTag: String) {
        val clickedUser =
            chatUsers.firstOrNull { it.nickname.equals(userTag.getNicknameFromLink(), true) }
        if (clickedUser != null) {
            sendEvent(
                ConversationEvent.ShowPersonDetailDialog(
                    PersonDetail(
                        userId = clickedUser.id,
                        avatar = clickedUser.avatar,
                        phone = clickedUser.phone,
                        userName = clickedUser.name,
                        userNickname = clickedUser.nickname
                    )
                )
            )
        }
    }

    fun fetchTags(text: String, selectionStart: Int) {
        var users = emptyList<User>()
        if (text.isNotEmpty() && selectionStart != 0 && text.contains('@')) {
            val prevTag = text.getOrNull(selectionStart - 2)
            val canShowTagList = if (prevTag == null) text.startsWith('@') else prevTag == ' '
            if (text.getOrNull(selectionStart - 1) == '@' && canShowTagList) {
                users = chatUsers.filter { it.nickname.isNotEmpty() && it.id != selfUserId }
            } else {
                val tagPos = text.lastIndexOf('@')
                val nickname = text.substring(tagPos, selectionStart)
                if (USER_TAG_PATTERN.matcher(nickname).matches() && (text.getOrNull(tagPos - 1)
                        ?: ' ') == ' '
                ) {
                    users = chatUsers.filter {
                        it.nickname.contains(
                            nickname.getNicknameFromLink(),
                            true
                        ) && it.id != selfUserId
                    }
                }
            }
        }
        sendEvent(ConversationEvent.ShowUsersTags(users))
    }

    fun onUserClicked(editMode: Boolean) {
        sendEvent(ConversationEvent.ToggleToolbarClickable(clickable = false))
        profileOpenJob = viewModelScope.launch {
            if (chatId != null)
                messengerInteractor.getChatById(chatId!!).onSuccess { chat ->
                    chatUsers = chat.chatUsers
                    permittedReactions = chat.mapPermittedReactions()
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
                            editMode = editMode
                        )
                    )
                }
            sendEvent(ConversationEvent.ToggleToolbarClickable(clickable = true))
        }
    }

    fun closeSocket() {
        messengerInteractor.emitChatListeners(null, chatId)
        messengerInteractor.closeSocket()
    }

    private suspend fun subscribeMessages(isResumed: Boolean, chatId: Long, selfUserId: Long) {
        loadPage(isResumed)
        messengerInteractor.getMessagesFromChat(
            id = chatId,
            selfUserId = selfUserId,
            onNewMessageStatusCallback = { onNewMessageStatus(it) },
            onMessageDeletedCallback = { onMessagesDeletedCallback(it) },
            onNewChatChangesCallback = { onChatChangesCallback(it) },
            onChatDeletedCallback = { onChatDeletedCallback(it) },
            onNewReactionCallback = { onNewReaction(it) }
        )
            .flowOn(Dispatchers.Main)
            .onEach { message ->
                loadItemsJob?.join()
                updateState { state ->
                    if (message.forwardedMessages.isNullOrEmpty() && message.isEdited && state.messages.filter { it.value.id == message.id }
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

                    val newMessages = listOf(message).toConversationItems(
                        selfUserId = selfUserId,
                        isGroup = params.chat.isGroup,
                        storageUrlConverter
                    )
                    state.copy(
                        messages = newMessages.plus(state.messages)
                    )
                }
                if (message.messageType == MESSAGE_TYPE_SYSTEM) {
                    messengerInteractor.getChatById(chatId).onSuccess {
                        chatUsers = it.chatUsers
                        permittedReactions = it.mapPermittedReactions()
                        updateState { state ->
                            state.copy(
                                avatar = it.avatar,
                                userName = PrintableText.Raw(it.chatName),
                                userStatus = UserStatus.OnlineStatus.toPrintableText(
                                    "",
                                    params.chat.isGroup,
                                    chatUsers
                                ),
                            )
                        }
                    }
                }
                if (firstVisibleItemPosition < 2) {
                    messengerInteractor.emitMessageRead(chatId, listOf(message.id))
                    sendEvent(ConversationEvent.UpdateScrollPosition(0))
                } else if (selfUserId != message.userSenderId) {
                    updateState { state ->
                        state.copy(
                            newMessagesCount = state.newMessagesCount + 1,
                            userStatus = UserStatus.OnlineStatus.toPrintableText(
                                "",
                                params.chat.isGroup,
                                chatUsers
                            ),
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
        messengerInteractor.getChatById(chatId).onSuccess {
            chatUsers = it.chatUsers
            permittedReactions = it.mapPermittedReactions()
            updateState { state ->
                state.copy(
                    avatar = it.avatar,
                    userName = PrintableText.Raw(it.chatName),
                    userStatus = UserStatus.OnlineStatus.toPrintableText(
                        "",
                        params.chat.isGroup,
                        chatUsers
                    ),
                )
            }
        }
    }

    private var lastDotsStatus = PrintableText.Raw(".")

    private fun subscribeMessageActions() {
        messengerInteractor.messageActionsFlow
            .flowOn(Dispatchers.Main)
            .onEach { messageAction ->
                if (chatId == messageAction.chatId) {
                    sendEvent(
                        ConversationEvent.DotsEvent(
                            userStatus = messageAction.userStatus.toPrintableText(
                                messageAction.userName,
                                params.chat.isGroup, chatUsers
                            ),
                            userStatusDots = PrintableText.Raw(".")
                        )
                    )
                    repeat(7) {
                        delay(300)
                        val userStatusDots = getPrintableRawText(lastDotsStatus)
                        val newDotCount = userStatusDots.count() % 3 + 1
                        lastDotsStatus = PrintableText.Raw(".".repeat(newDotCount))
                        sendEvent(
                            ConversationEvent.DotsEvent(
                                userStatus = messageAction.userStatus.toPrintableText(
                                    messageAction.userName,
                                    params.chat.isGroup,
                                    chatUsers
                                ),
                                userStatusDots = lastDotsStatus
                            )
                        )
                    }
                    sendEvent(
                        ConversationEvent.DotsEvent(
                            userStatus = UserStatus.OnlineStatus.toPrintableText(
                                messageAction.userName,
                                params.chat.isGroup,
                                chatUsers
                            ),
                            userStatusDots = PrintableText.EMPTY
                        )
                    )
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
        updateState { state ->
            val messages: MutableMap<String, ConversationViewItem> = mutableMapOf()
            currentViewState.messages.entries.forEach { item ->
                if (!deletedMessages.contains(item.value.id))
                    messages[item.key] = item.value
            }
            when (state.inputMessageMode) {
                is InputMessageMode.Edit -> {
                    if (deletedMessages.contains(state.inputMessageMode.messageToEdit.id)) {
                        return@updateState state.copy(
                            messages = messages,
                            inputMessageMode = InputMessageMode.Default("")
                        )
                    }
                }
                is InputMessageMode.Reply -> {
                    if (deletedMessages.contains(state.inputMessageMode.messageToReply.id)) {
                        return@updateState state.copy(
                            messages = messages,
                            inputMessageMode = InputMessageMode.Default()
                        )
                    }
                }
                is InputMessageMode.Default -> {}
                is InputMessageMode.Forward -> {}
            }
            state.copy(messages = messages)
        }
    }

    private fun onChatChangesCallback(chatChanges: ChatChanges) {
        //TODO изменение имени и аватара чата
        with(chatChanges) {
            users?.let { users ->
                chatUsers = users
                if (selfUserId !in users.map { user -> user.id }) {
                    exitToMessenger()
                }
            }
        }
    }

    private fun onChatDeletedCallback(deletedChatId: Long) {
        if (chatId == deletedChatId && !userDeleteChat)
            sendEvent(
                ConversationEvent.ShowChatDeletedDialog
            )
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
        viewModelScope.launch {
            val permissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionInterface.request(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                permissionInterface.request(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if(permissionGranted){
                val fileState = (currentViewState.inputMessageMode as? InputMessageMode.Default) ?: return@launch
                val allImages = fileState.attachedFiles.filterIsInstance<AttachedFile.Image>()
                fileSelectorLauncher.launch(
                    FileSelectorNavigationContract.Params(
                        selectedImages = allImages.toContentUriList()
                    )
                )
            }
        }
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
                features.cameraFeature.saveFile(it, tempFile)

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
            when (state.inputMessageMode) {
                is InputMessageMode.Default -> {
                    return@updateState state.copy(
                        inputMessageMode = state.inputMessageMode.copy(
                            attachedFiles = state.inputMessageMode.attachedFiles.filter { it != attachedFile })
                    )
                }
                else -> return@updateState state
            }
        }
    }

    fun onOpenMenu() {
        sendEvent(ConversationEvent.OpenMenuEvent)
    }

    fun showDialog() {
        chatId?.let { sendEvent(ConversationEvent.ShowDeleteChatDialog(it)) }
    }

    fun deleteChat(id: Long) {
        userDeleteChat = true
        viewModelScope.launch {
            if (messengerInteractor.deleteChat(id).successOrSendError() != null)
                exitWithResult(
                    ConversationNavigationContract.createResult(
                        ConversationNavigationContract.Result.ChatDeleted(id)
                    )
                )
            userDeleteChat = false
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
                    selfViewItem.loadableContent?.map { AttachedFile.Image(it) } ?: return
                )
            }
            is ConversationViewItem.Self.Document -> {
                sendFiles(
                    selfViewItem.files?.map { AttachedFile.Document(it) } ?: return
                )
            }
            is ConversationViewItem.Self.Forward -> {}
            is ConversationViewItem.Self.TextReplyOnImage -> {}
        }
    }

    fun onCancelSentClicked(selfViewItem: ConversationViewItem.Self) {
        updateState { state ->
            state.copy(
                messages = state.messages.filter { it.key != selfViewItem.localId }
            )
        }
    }

    fun onImagesClicked(
        meta: List<MetaInfo>,
        currImagePosition: Int,
    ) {
        if (meta.isEmpty() || chatId == null) return
        val imageParams = ImageViewerContract.ImageViewerParams.ImagesParams(
            imageViewerModel = meta.map { ImageViewerModel(it.url, null) },
            currentImagePosition = currImagePosition
        )
        imageViewerLauncher.launch(imageParams)
    }


    fun onImageClicked(
        url: String? = null,
        conversationViewItem: ConversationViewItem? = null,
        galleryImage: GalleryImage? = null,
    ) {
        val imageParams = when {
            url != null -> {
                ImageViewerContract.ImageViewerParams.ImageParams(
                    ImageViewerModel(url, null)
                )
            }
            galleryImage != null -> {
                ImageViewerContract.ImageViewerParams.ImageParams(
                    imageModel = ImageViewerModel(
                        null, null, imageGallery = galleryImage
                    )
                )
            }
            chatId != null && conversationViewItem != null -> {
                val mess = conversationViewItem.replyMessage ?: conversationViewItem
                ImageViewerContract.ImageViewerParams.MessageImageParams(
                    imageViewerModel = listOf(ImageViewerModel(mess.content as? String, null)),
                    messageId = mess.id,
                    canDelete = conversationViewItem.canDelete(),
                    username = conversationViewItem.userName
                )
            }
            else -> {
                return
            }
        }
        imageViewerLauncher.launch(imageParams)
    }

    fun onSelfMessageLongClicked(conversationViewItem: ConversationViewItem.Self) {
        when (conversationViewItem.sentStatus) {
            SentStatus.Error -> sendEvent(ConversationEvent.ShowErrorSentDialog(conversationViewItem))
            SentStatus.Sent,
            SentStatus.Received,
            SentStatus.Read,
            -> sendEvent(ConversationEvent.ShowSelfMessageActionDialog(conversationViewItem, permittedReactions))
            SentStatus.Loading -> Unit
            SentStatus.None -> Unit
        }
    }

    fun onDeleteMessageClicked(conversationViewItem: ConversationViewItem.Self) {
        deleteMessage(conversationViewItem.id)
    }

    private fun deleteMessage(messageId: Long) {
        viewModelScope.launch {
            messengerInteractor.deleteMessage(
                messageId = messageId,
                chatId = chatId ?: return@launch
            ).onSuccess {
                updateState { state ->
                    val messages: MutableMap<String, ConversationViewItem> = mutableMapOf()
                    currentViewState.messages.entries.forEach { item ->
                        if (item.value.id != messageId)
                            messages[item.key] = item.value
                    }
                    when (state.inputMessageMode) {
                        is InputMessageMode.Edit -> {
                            if (state.inputMessageMode.messageToEdit.id == messageId) {
                                return@updateState state.copy(
                                    messages = messages,
                                    inputMessageMode = InputMessageMode.Default("")
                                )
                            }
                        }
                        is InputMessageMode.Reply -> {
                            if (state.inputMessageMode.messageToReply.id == messageId) {
                                return@updateState state.copy(
                                    messages = messages,
                                    inputMessageMode = InputMessageMode.Default()
                                )
                            }
                        }
                        is InputMessageMode.Default -> {}
                        is InputMessageMode.Forward -> {}
                    }
                    return@updateState state.copy(messages = messages)
                }
            }
        }
    }

    fun onReceiveMessageLongClicked(conversationViewItem: ConversationViewItem.Receive) {
        sendEvent(ConversationEvent.ShowReceiveMessageActionDialog(conversationViewItem, permittedReactions))
    }

    fun onGroupMessageLongClicked(conversationViewItem: ConversationViewItem.Group) {
        sendEvent(ConversationEvent.ShowGroupMessageActionDialog(conversationViewItem, permittedReactions))
    }

    fun onReceiveTextReplyImageLongClicked(conversationViewItem: ConversationViewItem.Receive.TextReplyOnImage) {
        sendEvent(ConversationEvent.ShowReceiveMessageActionDialog(conversationViewItem, permittedReactions))
    }

    fun onSelfForwardLongClicked(conversationViewItem: ConversationViewItem.Self.Forward) {
        sendEvent(ConversationEvent.ShowSelfMessageActionDialog(conversationViewItem, permittedReactions))
    }

    fun onReceiveForwardLongClicked(conversationViewItem: ConversationViewItem.Receive.Forward) {
        sendEvent(ConversationEvent.ShowReceiveMessageActionDialog(conversationViewItem, permittedReactions))
    }

    fun onGroupForwardLongClicked(conversationViewItem: ConversationViewItem.Group.Forward) {
        sendEvent(ConversationEvent.ShowGroupMessageActionDialog(conversationViewItem, permittedReactions))
    }

    fun onGroupDocumentLongClicked(conversationViewItem: ConversationViewItem.Group) {
        sendEvent(ConversationEvent.ShowGroupMessageActionDialog(conversationViewItem, permittedReactions))
    }

    fun onSelfDocumentLongClicked(conversationViewItem: ConversationViewItem.Self) {
        sendEvent(ConversationEvent.ShowSelfMessageActionDialog(conversationViewItem, permittedReactions))
    }

    fun onReceiveDocumentLongClicked(conversationViewItem: ConversationViewItem.Receive) {
        sendEvent(ConversationEvent.ShowReceiveMessageActionDialog(conversationViewItem, permittedReactions))
    }

    fun onTypingMessage() {
        messengerInteractor.emitMessageAction(chatId.toString(), TYPING_MESSAGE_STATUS)
    }

    fun onDownloadDocument(
        meta: MetaInfo,
        progressListener: ProgressListener,
    ) {
        val documentLink = meta.url.toString()
        downloadFileJob = viewModelScope.launch {
            downloadDocumentUseCase.invoke(
                documentLink = documentLink,
                progressListener = progressListener,
                metaInfo = meta,
                permissionInterface = permissionInterface,
                openFileLauncher = openFileLauncher
            )
        }
    }

    fun updateScrollPosition() {
        sendEvent(ConversationEvent.UpdateScrollPosition(currentViewState.newMessagesCount))
    }

    var isSubscribed = false

    fun onScrolledToLastPendingMessage(firstVisibleItem: Int) {
        firstVisibleItemPosition = firstVisibleItem

        if (firstVisibleItemPosition > 0 && isSubscribed) {
            messengerInteractor.emitChatListeners(null, chatId)
            isSubscribed = false
        } else if (firstVisibleItemPosition == 0) {
            messengerInteractor.emitChatListeners(chatId, null)
            val readMessages = currentViewState.messages.values.filter {
                it.sentStatus == SentStatus.Received
            }.map { it.id }
            if (readMessages.isNotEmpty()) {
                messengerInteractor.emitMessageRead(
                    chatId!!,
                    readMessages
                )
            }
            isSubscribed = true
        }

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

    fun onViewStopped() {
        loadItemsJob = null
    }

    fun contentInserted(content: Content) {
        attachContentFile(content)
    }

    fun postReaction(messageId: Long, reaction: String) {
        viewModelScope.launch {
            chatId?.let {
                messengerInteractor.postReaction(it, messageId, reaction.dropLast(1))
            }
        }
    }

    fun onNewReaction(messageReactions: MessageReactions) {
        val changedMessages = currentViewState.messages.mapValues {
            if (it.value.id == messageReactions.messageId)
                messageReactions.toConversationViewItem(selfUserId, it.value)
            else it.value

        }
        updateState { state -> state.copy(messages = changedMessages) }
    }

}