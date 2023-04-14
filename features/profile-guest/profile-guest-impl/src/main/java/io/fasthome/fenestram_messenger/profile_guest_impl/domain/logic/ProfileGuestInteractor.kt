package io.fasthome.fenestram_messenger.profile_guest_impl.domain.logic

import io.fasthome.fenestram_messenger.messenger_api.entity.MessageType
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.repo.ProfileGuestRepo
import java.util.*

class ProfileGuestInteractor(private val profileGuestRepo: ProfileGuestRepo) {

    suspend fun patchChatAvatar(id: Long, avatar: String) =
        profileGuestRepo.patchChatAvatar(id, avatar)

    suspend fun patchChatName(id: Long, name: String) = profileGuestRepo.patchChatName(id, name)

    suspend fun uploadChatAvatar(photoBytes: ByteArray) =
        profileGuestRepo.uploadImage(photoBytes, UUID.randomUUID().toString())

    suspend fun getAttachImages(chatId : Long) = profileGuestRepo.getAttachFiles(chatId = chatId, messageType = MessageType.Image)

    suspend fun getAttachFiles(chatId : Long) = profileGuestRepo.getAttachFiles(chatId = chatId, messageType = MessageType.Document)

}