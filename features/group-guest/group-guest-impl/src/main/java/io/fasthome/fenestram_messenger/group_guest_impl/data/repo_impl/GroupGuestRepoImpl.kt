package io.fasthome.fenestram_messenger.group_guest_impl.data.repo_impl

import io.fasthome.fenestram_messenger.group_guest_impl.data.service.GroupGuestService
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.model.AddUsersToChatRequest
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.model.RemoveUsersInChatRequest
import io.fasthome.fenestram_messenger.group_guest_impl.domain.repo.GroupGuestRepo
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult

class GroupGuestRepoImpl(private val groupGuestService: GroupGuestService) : GroupGuestRepo {
    override suspend fun addUsersToChat(
        chatId: Long?,
        usersId: AddUsersToChatRequest,
    ): CallResult<List<ParticipantsViewItem>> =
        callForResult {
            groupGuestService.addUsersToChat(chatId, usersId)
        }

    override suspend fun deleteUserFromChat(
        idChat: Long,
        idUsers: RemoveUsersInChatRequest,
    ): CallResult<List<ParticipantsViewItem>> = callForResult {
        groupGuestService.deleteUserFromChat(idChat, idUsers)
    }

    override suspend fun getUserId() = callForResult {
        groupGuestService.getUserId()
    }
}