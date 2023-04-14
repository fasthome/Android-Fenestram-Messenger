package io.fasthome.fenestram_messenger.group_guest_impl.domain.repo

import io.fasthome.fenestram_messenger.group_guest_impl.data.service.model.AddUsersToChatRequest
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.model.RemoveUsersInChatRequest
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.util.CallResult

interface GroupGuestRepo {
    suspend fun addUsersToChat(
        chatId: Long?,
        usersId: AddUsersToChatRequest
    ): CallResult<List<ParticipantsViewItem>>

    suspend fun deleteUserFromChat(
        idChat: Long,
        idUsers: RemoveUsersInChatRequest
    ): CallResult<List<ParticipantsViewItem>>

    suspend fun getUserId(): CallResult<Long?>
}