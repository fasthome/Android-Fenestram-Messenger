package io.fasthome.fenestram_messenger.profile_guest_impl.data.service

import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_api.entity.MessageType
import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model.FileResponse
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UrlLoadableContent

class FilesMapper(private val storageUrlConverter: StorageUrlConverter) {

    fun mapResponseToFiles(fileResponse: FileResponse, messageType: String): List<FileItem> {
        val files = fileResponse.files.mapNotNull { responseItem ->
            when (messageType) {
                MessageType.Image.type -> {
                    FileItem.Image(UrlLoadableContent(storageUrlConverter.convert(responseItem)))
                }
                MessageType.Document.type -> {
                    FileItem.Document(responseItem)
                }
                else -> {
                    null
                }
            }
        }
        return files
    }

}