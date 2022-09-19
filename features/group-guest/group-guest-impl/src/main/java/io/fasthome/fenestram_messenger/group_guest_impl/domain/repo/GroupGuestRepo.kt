package io.fasthome.fenestram_messenger.group_guest_impl.domain.repo

import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.util.CallResult

interface GroupGuestRepo {
    suspend fun addUsersToChat(
        chatId: Long?,
        usersId: List<Long>?
    ): CallResult<List<ParticipantsViewItem>>

    suspend fun deleteUserFromChat(
        idChat: Long,
        idUser: Long
    ): CallResult<List<ParticipantsViewItem>>

    suspend fun getUserId(): CallResult<Long?>
}