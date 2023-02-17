package io.fasthome.fenestram_messenger.profile_guest_impl.data.service

import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model.FileResponse
import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model.PatchChatAvatarRequest
import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model.PatchChatNameRequest
import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model.UploadImageResponse
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.UploadImageResult
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.util.requireData

class ProfileGuestService(
    clientFactory: NetworkClientFactory,
    private val environment: Environment,
    private val filesMapper: FilesMapper
) {
    private val client = clientFactory.create()

    suspend fun patchChatAvatar(id: Long, avatar: String) {
        client.runPatch<PatchChatAvatarRequest, Unit>(
            path = "${environment.endpoints.baseUrl}api/v1/chats/$id/avatar",
            body = PatchChatAvatarRequest(avatar),
            useBaseUrl = false
        )
    }

    suspend fun patchChatName(id: Long, name: String) {
        client.runPatch<PatchChatNameRequest, Unit>(
            path = "chats/$id/name",
            body = PatchChatNameRequest(name)
        )
    }

    suspend fun uploadImage(photoBytes: ByteArray, guid: String): UploadImageResult {
        val response = client
            .runSubmitFormWithFile<BaseResponse<UploadImageResponse>>(
                path = "files/upload",
                binaryData = photoBytes,
                filename = "$guid.jpg",
            )
            .requireData()
        return UploadImageResult(imagePath = response.pathToFile)
    }

    suspend fun getAttachFiles(chatId: Long, type: String): List<FileItem> {
        return client.runGet<BaseResponse<FileResponse>>(
            path = "chats/$chatId/files/$type",
        ).requireData().let { filesMapper.mapResponseToFiles(it, messageType = type) }
    }
}