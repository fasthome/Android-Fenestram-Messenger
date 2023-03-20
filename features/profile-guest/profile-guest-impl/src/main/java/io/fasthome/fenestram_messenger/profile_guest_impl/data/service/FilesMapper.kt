package io.fasthome.fenestram_messenger.profile_guest_impl.data.service

import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_api.entity.MessageType
import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model.FileResponse
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UrlLoadableContent
import io.fasthome.fenestram_messenger.util.model.MetaInfo

class FilesMapper(private val storageUrlConverter: StorageUrlConverter) {

    fun mapResponseToFiles(fileResponse: FileResponse, messageType: String): List<FileItem> {
        val files = fileResponse.files.mapNotNull { responseItem ->
            when (messageType) {
                MessageType.Image.type -> {
                    FileItem.Image(UrlLoadableContent(storageUrlConverter.convert(responseItem.url)))
                }
                MessageType.Document.type -> {
                    FileItem.Document(
                        MetaInfo(
                            name = responseItem.name,
                            extension = responseItem.extension,
                            size = responseItem.size,
                            url = storageUrlConverter.convert(responseItem.url)
                        )
                    )
                }
                else -> {
                    null
                }
            }
        }
        return files
    }

}