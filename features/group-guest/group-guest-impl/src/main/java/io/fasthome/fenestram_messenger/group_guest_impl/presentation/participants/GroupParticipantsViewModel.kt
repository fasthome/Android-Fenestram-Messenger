package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import io.fasthome.component.imageViewer.ImageViewerContract
import io.fasthome.component.person_detail.PersonDetail
import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.group_guest_api.GroupParticipantsInterface
import io.fasthome.fenestram_messenger.group_guest_api.ParticipantsParams
import io.fasthome.fenestram_messenger.group_guest_impl.domain.logic.GroupGuestInteractor
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.GroupGuestContract
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.mapper.participantsViewItemToDifferentUsers
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.mapper.userToParticipantsItem
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch

class GroupParticipantsViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ParticipantsParams,
    private val groupGuestInteractor: GroupGuestInteractor,
    private val contactsFeature: ContactsFeature,
    private val messengerFeature: MessengerFeature,
    private val profileImageUrlConverter: StorageUrlConverter
) : BaseViewModel<GroupParticipantsState, GroupParticipantsEvent>(router, requestParams),
    GroupParticipantsInterface {

    private var userId: Long? = null

    private val imageViewerLauncher = registerScreen(ImageViewerContract) {}

    private val conversationLauncher =
        registerScreen(messengerFeature.conversationNavigationContract) { }

    init {
        viewModelScope.launch {
            userId = groupGuestInteractor.getUserId().successOrSendError()
            updateState { state ->
                state.copy(participants = params.participants.map {
                    userToParticipantsItem(it, userId)
                })
            }
        }
    }

    private val addUserToChatLauncher = registerScreen(GroupGuestContract) { result ->
        when (result) {
            is GroupGuestContract.Result.UsersAdded -> {
                updateState { state ->
                    state.copy(participants = result.users.map {
                        participantsViewItemToDifferentUsers(it, userId)
                    })
                }
            }
            is GroupGuestContract.Result.Canceled -> {}
        }
        exitWithResult(GroupGuestContract.createResult(result))
    }

    private val addUserToContactsLauncher =
        registerScreen(contactsFeature.contactAddNavigationContract) { result ->
            when (result) {
                is ContactsFeature.ContactAddResult.Success -> {
                    viewModelScope.launch {
                        contactsFeature.getContactsAndUploadContacts()
                    }
                }
                is ContactsFeature.ContactAddResult.Canceled -> {}
            }
        }

    override fun createInitialState(): GroupParticipantsState {
        return GroupParticipantsState(listOf())
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
        params.participants.find { user ->
            user.id == id
        }?.let {
            sendEvent(GroupParticipantsEvent.MenuOpenEvent(id, it.nickname, it.phone))
        }
    }

    fun onDeleteUserClicked(id: Long) {
        viewModelScope.launch {
            groupGuestInteractor.deleteUserFromChat(params.chatId!!, id).successOrSendError()?.let {
                if (userId == id)
                    router.backTo(null)
                else
                    updateState { state ->
                        state.copy(participants = it.map { item ->
                            participantsViewItemToDifferentUsers(item, userId)
                        })
                    }
            }
        }
    }

    fun onAddToContactsClicked(name: String, phone: String) {
        addUserToContactsLauncher.launch(ContactsFeature.Params(name, phone))
    }

    fun onAnotherUserClicked(userId: Long) {
        val selectedUser = params.participants.find { it.id == userId }!!
        val userName =
            if (!selectedUser.contactName.isNullOrEmpty()) selectedUser.contactName else selectedUser.name
        sendEvent(
            GroupParticipantsEvent.ShowPersonDetailDialog(
                PersonDetail(
                    userId = userId,
                    userName = userName!!,
                    userNickname = selectedUser.nickname,
                    avatar = selectedUser.avatar,
                    phone = selectedUser.phone
                )
            )
        )
    }


    fun onImageClicked(url: String? = null, bitmap: Bitmap? = null) {
        imageViewerLauncher.launch(ImageViewerContract.ImageViewerParams.Params(url, bitmap))
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