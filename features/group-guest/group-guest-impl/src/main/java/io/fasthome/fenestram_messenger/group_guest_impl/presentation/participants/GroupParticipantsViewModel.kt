package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants

import android.graphics.Typeface
import android.view.View
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.group_guest_api.GroupParticipantsInterface
import io.fasthome.fenestram_messenger.group_guest_api.ParticipantsParams
import io.fasthome.fenestram_messenger.group_guest_impl.domain.logic.GroupGuestInteractor
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.GroupGuestContract
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.mapper.userToParticipantsItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch

class GroupParticipantsViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ParticipantsParams,
    private val groupGuestInteractor: GroupGuestInteractor,
) : BaseViewModel<GroupParticipantsState, GroupParticipantsEvent>(router, requestParams),
    GroupParticipantsInterface {

    private var userId: Long? = null

    init {
        viewModelScope.launch {
            userId = groupGuestInteractor.getUserId().successOrSendError()
        }
    }

    private val addUserToChatLauncher = registerScreen(GroupGuestContract) { result ->
        when (result) {
            is GroupGuestContract.Result.UsersAdded -> {
                updateState { state ->
                    state.copy(participants = result.users)
                }
            }
            is GroupGuestContract.Result.Canceled -> {}
        }
        exitWithResult(GroupGuestContract.createResult(result))
    }

    override fun createInitialState(): GroupParticipantsState {
        return GroupParticipantsState(
            participants = params.participants.map(::userToParticipantsItem)
        )
    }

    fun onAddUserToChat() {
        addUserToChatLauncher.launch(
            GroupGuestContract.Params(
                currentViewState.participants,
                params.chatId!!
            )
        )
    }

    fun onMenuClicked(id: Long, view: View) {
        sendEvent(GroupParticipantsEvent.MenuOpenEvent(id, view))
    }

    fun onDeleteUserClicked(id: Long) {
        viewModelScope.launch {
            groupGuestInteractor.deleteUserFromChat(params.chatId!!, id).successOrSendError()?.let {
                if (userId == id)
                    router.backTo(null)
                else
                    updateState { state -> state.copy(participants = it) }
            }
        }
    }

}