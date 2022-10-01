package io.fasthome.fenestram_messenger.profile_guest_impl.domain.repo

import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.UploadImageResult
import io.fasthome.fenestram_messenger.util.CallResult

interface ProfileGuestRepo {

    suspend fun patchChatAvatar(id: Long, avatar: String): CallResult<Unit>

    suspend fun patchChatName(id: Long, name: String): CallResult<Unit>

    suspend fun uploadImage(photoBytes: ByteArray, guid: String): CallResult<UploadImageResult>

}