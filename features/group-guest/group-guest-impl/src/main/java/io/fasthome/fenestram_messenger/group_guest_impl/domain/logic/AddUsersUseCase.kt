package io.fasthome.fenestram_messenger.group_guest_impl.domain.logic

import io.fasthome.fenestram_messenger.group_guest_impl.domain.repo.GroupGuestRepo
import io.fasthome.fenestram_messenger.util.CallResult

class AddUsersUseCase(private val groupGuestRepo: GroupGuestRepo) {
    suspend fun addUsersToChat(chatId: Long?, usersId: List<Long>?) =
        groupGuestRepo.addUsersToChat(chatId, usersId)
}