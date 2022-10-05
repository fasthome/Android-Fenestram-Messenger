package io.fasthome.fenestram_messenger.profile_guest_impl.data.service

import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model.PatchChatAvatarRequest
import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model.PatchChatNameRequest
import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model.UploadImageResponse
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.UploadImageResult
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.util.requireData

class ProfileGuestService(clientFactory: NetworkClientFactory) {
    private val client = clientFactory.create()

    suspend fun patchChatAvatar(id: Long, avatar: String) {
        client.runPatch<PatchChatAvatarRequest, Unit>(
            path = "api/v1/chats/$id/avatar",
            body = PatchChatAvatarRequest(avatar)
        )
    }

    suspend fun patchChatName(id: Long, name: String) {
        client.runPatch<PatchChatNameRequest, Unit>(
            path = "api/v1/chats/$id/name",
            body = PatchChatNameRequest(name)
        )
    }

    suspend fun uploadImage(photoBytes: ByteArray, guid: String): UploadImageResult {
        val response = client
            .runSubmitFormWithFile<BaseResponse<UploadImageResponse>>(
                path = "api/v1/files/upload",
                binaryData = photoBytes,
                filename = "$guid.jpg",
            )
            .requireData()
        return UploadImageResult(imagePath = response.pathToFile)
    }
}