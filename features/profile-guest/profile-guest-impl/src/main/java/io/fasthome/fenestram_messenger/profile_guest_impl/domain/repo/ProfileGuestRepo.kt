package io.fasthome.fenestram_messenger.profile_guest_impl.domain.repo

import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.DeleteChatResult
import io.fasthome.fenestram_messenger.util.CallResult

interface ProfileGuestRepo {
    suspend fun deleteChat(id: Long): CallResult<DeleteChatResult>
}