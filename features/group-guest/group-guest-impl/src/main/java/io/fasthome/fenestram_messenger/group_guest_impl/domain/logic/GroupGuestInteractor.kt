package io.fasthome.fenestram_messenger.group_guest_impl.domain.logic

import io.fasthome.fenestram_messenger.group_guest_impl.data.service.model.AddUsersToChatRequest
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.model.RemoveUsersInChatRequest
import io.fasthome.fenestram_messenger.group_guest_impl.domain.repo.GroupGuestRepo

class GroupGuestInteractor(private val groupGuestRepo: GroupGuestRepo) {
    suspend fun addUsersToChat(chatId: Long?, usersId: List<Long>) =
        groupGuestRepo.addUsersToChat(chatId, AddUsersToChatRequest(usersId))

    suspend fun deleteUserFromChat(chatId: Long, userId: Long) =
        groupGuestRepo.deleteUserFromChat(chatId, RemoveUsersInChatRequest(listOf(userId)))

    suspend fun getUserId() = groupGuestRepo.getUserId()
}