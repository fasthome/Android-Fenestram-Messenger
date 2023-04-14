package io.fasthome.fenestram_messenger.profile_guest_impl.domain.repo

import io.fasthome.fenestram_messenger.messenger_api.entity.MessageType
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.UploadImageResult
import io.fasthome.fenestram_messenger.util.CallResult

interface ProfileGuestRepo {

    suspend fun patchChatAvatar(id: Long, avatar: String): CallResult<Unit>

    suspend fun patchChatName(id: Long, name: String): CallResult<Unit>

    suspend fun uploadImage(photoBytes: ByteArray, guid: String): CallResult<UploadImageResult>

    suspend fun getAttachFiles(chatId : Long, messageType: MessageType) : CallResult<List<FileItem>>

}