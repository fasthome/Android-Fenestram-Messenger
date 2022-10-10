package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import io.fasthome.component.person_detail.PersonDetail
import io.fasthome.component.camera.CameraComponentParams
import io.fasthome.component.pick_file.PickFileInterface
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.camera_api.CameraFeature
import io.fasthome.fenestram_messenger.camera_api.CameraParams
import io.fasthome.fenestram_messenger.camera_api.ConfirmParams
import io.fasthome.fenestram_messenger.camera_api.ConfirmResult
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.data.ProfileImageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.MessagesPage
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.AttachedFile
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.CapturedItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.SentStatus
import io.fasthome.fenestram_messenger.messenger_impl.presentation.imageViewer.ImageViewerContract
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.Message
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper.Companion.PAGE_SIZE
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.kotlin.switchJob
import io.fasthome.fenestram_messenger.util.model.Bytes
import io.fasthome.fenestram_messenger.util.model.Bytes.Companion.BYTES_PER_MB
import kotlinx.coroutines.Dispatchers
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
    private val profileImageUrlConverter: ProfileImageUrlConverter
) : BaseViewModel<ConversationState, ConversationEvent>(router, requestParams) {

    private val imageViewerLauncher = registerScreen(ImageViewerContract)

    private val cameraLauncher = registerScreen(features.cameraFeature.cameraNavigationContract) { result ->
        val tempFile = result.tempFile

        photoPreviewLauncher.launch(
            ConfirmParams(
                content = Content.FileContent(tempFile),
            )
        )
    }

    private val photoPreviewLauncher = registerScreen(features.cameraFeature.confirmNavigationContract) { result ->
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
    private var lastPage: MessagesPage? = null

    init {
        pickFileInterface.resultEvents()
            .onEach {
                when (it) {
                    PickFileInterface.ResultEvent.PickCancelled -> Unit
                    is PickFileInterface.ResultEvent.Picked -> {
                        attachContentFile(Content.FileContent(it.tempFile))
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
            lastPage?.let {
                if (it.total <= PAGE_SIZE) {
                    return@launch
                }
            }

            messengerInteractor.getChatPageItems(isResumed, chatId ?: return@launch).onSuccess {
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

    fun fetchMessages(isResumed: Boolean) {
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
                isResumed = isResumed,
                chatId ?: params.chat.id ?: return@launch,
                selfUserId ?: return@launch
            )
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
            userOnline = false,
            isChatEmpty = false,
            avatar = profileImageUrlConverter.convert(params.chat.avatar),
            attachedFiles = listOf()
        )
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
            sendImages(attachedFiles)
        }

        if (mess.isNotEmpty()) {
            sendMessage(mess)
        }
    }

    private fun sendImages(attachedFiles: List<AttachedFile.Image>) {
        val messages = currentViewState.messages

        val tempMessages = attachedFiles.map {
            createImageMessage(null, it.content)
        }

        updateState { state ->
            state.copy(attachedFiles = listOf())
        }
        viewModelScope.launch {
            updateState { state ->
                state.copy(
                    messages = tempMessages.reversed().associateBy({
                        it.localId
                    }, {
                        it
                    }).plus(messages)
                )
            }
            tempMessages.forEach { tempMessage ->
                var imageUrl: String?

                val byteArray = when (val content = tempMessage.loadableContent) {
                    is Content.FileContent -> content.file.readBytes()
                    is Content.LoadableContent -> content.load()?.array
                    null -> return@launch
                }

                messengerInteractor.uploadProfileImage(
                    byteArray ?: return@launch
                )
                    .getOrNull()?.imagePath.let {
                        imageUrl = it
                        it
                    }
                val sendMessageResponse = messengerInteractor.sendMessage(
                    id = chatId ?: return@launch,
                    text = imageUrl ?: return@launch,
                    type = "image",
                    localId = tempMessage.localId,
                    authorId = selfUserId ?: return@launch
                )
                when (sendMessageResponse) {
                    is CallResult.Error -> {
                        updateStatus(
                            tempMessage,
                            SentStatus.Error,
                            profileImageUrlConverter.convert(imageUrl),
                        )
                    }
                    is CallResult.Success -> {
                        updateStatus(
                            tempMessage,
                            SentStatus.Sent,
                            profileImageUrlConverter.convert(imageUrl),
                            sendMessageResponse.data.id
                        )
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
            val sendMessageResponse = messengerInteractor.sendMessage(
                id = chatId ?: return@launch,
                text = mess,
                type = "text",
                localId = tempMessage.localId,
                authorId = selfUserId ?: return@launch
            )
            when (sendMessageResponse) {
                is CallResult.Error -> {
                    updateStatus(tempMessage, SentStatus.Error)
                }
                is CallResult.Success -> {
                    updateStatus(tempMessage, SentStatus.Sent, id = sendMessageResponse.data.id)
                }
            }
        }
        sendEvent(ConversationEvent.MessageSent)
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
                    profileGuestLauncher.launch(
                        ProfileGuestFeature.ProfileGuestParams(
                            id = chatId,
                            userName = chat.chatName,
                            userNickname = chat.chatUsers.firstOrNull { it.id != messengerInteractor.getUserId() }?.nickname
                                ?: "",
                            userAvatar = chat.avatar,
                            chatParticipants = chatUsers,
                            isGroup = params.chat.isGroup,
                            userPhone = chat.chatUsers.firstOrNull { it.id != messengerInteractor.getUserId() }?.phone
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
        sendEvent(
            ConversationEvent.ShowPersonDetailDialog(
                PersonDetail(
                    userId = item.userId,
                    avatar = item.avatar,
                    userName = getPrintableRawText(item.userName),
                    phone = item.phone,
                    userNickname = item.nickname
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
                    avatar = personDetail.avatar,
                    isGroup = false
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
                            selfViewItem.loadableContent ?: return
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

}