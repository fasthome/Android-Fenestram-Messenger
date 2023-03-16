package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants

import androidx.lifecycle.viewModelScope
import io.fasthome.component.image_viewer.ImageViewerContract
import io.fasthome.component.image_viewer.model.ImageViewerModel
import io.fasthome.component.person_detail.PersonDetail
import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.group_guest_api.GroupParticipantsInterface
import io.fasthome.fenestram_messenger.group_guest_api.ParticipantsParams
import io.fasthome.fenestram_messenger.group_guest_impl.domain.logic.GroupGuestInteractor
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.GroupGuestContract
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.mapper.userToParticipantsItem
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.getPrintableRawText
import kotlinx.coroutines.launch

class GroupParticipantsViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ParticipantsParams,
    private val groupGuestInteractor: GroupGuestInteractor,
    private val contactsFeature: ContactsFeature,
    private val messengerFeature: MessengerFeature,
    private val profileImageUrlConverter: StorageUrlConverter,
) : BaseViewModel<GroupParticipantsState, GroupParticipantsEvent>(router, requestParams),
    GroupParticipantsInterface {

    override lateinit var listChanged: (Int) -> Unit

    private var selfUserId: Long? = null

    private val imageViewerLauncher = registerScreen(ImageViewerContract) {}

    private val conversationLauncher =
        registerScreen(messengerFeature.conversationNavigationContract) { }

    init {
        viewModelScope.launch {
            selfUserId = groupGuestInteractor.getUserId().successOrSendError()
            updateState { state ->
                state.copy(participants = params.participants.map {
                    userToParticipantsItem(it, selfUserId)
                })
            }
        }
    }

    private val addUserToChatLauncher = registerScreen(GroupGuestContract) { result ->
        when (result) {
            is GroupGuestContract.Result.UsersAdded -> {}
            is GroupGuestContract.Result.Canceled -> {}
        }
        exitWithResult(GroupGuestContract.createResult(result))
    }

    private val addUserToContactsLauncher =
        registerScreen(contactsFeature.contactAddNavigationContract) { result ->
            when (result) {
                is ContactsFeature.ContactAddResult.Success -> {
                    viewModelScope.launch {
                        //contactsFeature.getContactsAndUploadContacts() // TODO: !!!
                    }
                }
                is ContactsFeature.ContactAddResult.Canceled -> {}
            }
        }

    override fun createInitialState(): GroupParticipantsState {
        return GroupParticipantsState(listOf())
    }

    fun subscribeToChatChanges() {
        viewModelScope.launch {
            selfUserId?.let { selfUserId = groupGuestInteractor.getUserId().successOrSendError() }
            messengerFeature.onChatChanges(params.chatId!!) { chatChanges ->
                chatChanges.users?.let { chatUsers ->
                    if (selfUserId !in chatUsers.map { user -> user.id }) {
                        router.backTo(null)
                    }

                    val selfChatUser = chatUsers.find { it.id == selfUserId }
                    val listWithSelfFirst = chatUsers.toMutableList()
                    listWithSelfFirst.remove(selfChatUser)
                    listWithSelfFirst.add(0, selfChatUser!!)
                    listChanged(chatUsers.size)

                    updateState { state ->
                        state.copy(participants = listWithSelfFirst.map {
                            userToParticipantsItem(it, selfUserId)
                        })
                    }
                }
            }
        }
    }

    fun onAddUserToChat() {
        addUserToChatLauncher.launch(
            GroupGuestContract.Params(
                currentViewState.participants,
                params.chatId!!
            )
        )
    }

    fun onMenuClicked(id: Long) {
        currentViewState.participants.find { user ->
            user.userId == id
        }?.let {
            sendEvent(
                GroupParticipantsEvent.MenuOpenEvent(
                    id,
                    getPrintableRawText(it.name),
                    it.phone
                )
            )
        }
    }

    fun onDeleteUserClicked(id: Long) {
        viewModelScope.launch {
            groupGuestInteractor.deleteUserFromChat(params.chatId!!, id).successOrSendError()?.let {
                listChanged(it.size)
                if (selfUserId == id)
                    router.backTo(null)
            }
        }
    }

    fun onAddToContactsClicked(name: String, phone: String) {
        addUserToContactsLauncher.launch(ContactsFeature.Params(name, phone))
    }

    fun onAnotherUserClicked(userId: Long) {
        currentViewState.participants.find { it.userId == userId }?.let { selectedUser ->
            sendEvent(
                GroupParticipantsEvent.ShowPersonDetailDialog(
                    PersonDetail(
                        userId = userId,
                        userName = getPrintableRawText(selectedUser.name),
                        userNickname = selectedUser.nickname,
                        avatar = selectedUser.avatar,
                        phone = selectedUser.phone
                    )
                )
            )
        }
    }


    fun onImageClicked(url: String? = null) {
        imageViewerLauncher.launch(ImageViewerContract.ImageViewerParams.ImageParams(ImageViewerModel(url, null)))
    }

    fun onLaunchConversationClicked(personDetail: PersonDetail) {
        conversationLauncher.launch(
            MessengerFeature.Params(
                userIds = listOf(personDetail.userId),
                chatName = personDetail.userName,
                avatar = profileImageUrlConverter.extractPath(personDetail.avatar),
                isGroup = false
            )
        )
    }


}