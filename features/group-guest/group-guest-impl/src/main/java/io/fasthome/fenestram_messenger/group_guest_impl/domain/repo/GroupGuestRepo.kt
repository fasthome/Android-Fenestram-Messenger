package io.fasthome.fenestram_messenger.group_guest_impl.domain.repo

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.util.CallResult

interface GroupGuestRepo {
    suspend fun addUsersToChat(
        chatId: Long?,
        usersId: List<Long>?
    ): CallResult<List<User>>
}