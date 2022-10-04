package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.group_guest_api.GroupParticipantsInterface
import io.fasthome.fenestram_messenger.group_guest_api.ParticipantsParams
import io.fasthome.fenestram_messenger.group_guest_impl.domain.logic.GroupGuestInteractor
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.GroupGuestContract
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.mapper.participantsViewItemToDifferentUsers
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.mapper.userToParticipantsItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import kotlinx.coroutines.launch

class GroupParticipantsViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ParticipantsParams,
    private val groupGuestInteractor: GroupGuestInteractor,
    private val contactsFeature: ContactsFeature,
    private val profileGuestFeature: ProfileGuestFeature,
) : BaseViewModel<GroupParticipantsState, GroupParticipantsEvent>(router, requestParams),
    GroupParticipantsInterface {

    private var userId: Long? = null

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

    private val profileGuestLauncher =
        registerScreen(profileGuestFeature.profileGuestNavigationContract) {}

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
        val selectedUser = params.participants.find { it.id == userId }
        val userName =
            if (selectedUser?.contactName?.isNotEmpty() == true) selectedUser.contactName else selectedUser?.name

        profileGuestLauncher.launch(
            ProfileGuestFeature.ProfileGuestParams(
                id = 0,
                userName = userName ?: "",
                userNickname = selectedUser?.nickname ?: "",
                userAvatar = selectedUser?.avatar ?: "",
                chatParticipants = listOf(),
                isGroup = false,
                userPhone = selectedUser?.phone ?: "",
                editMode = false
            )
        )
    }

}