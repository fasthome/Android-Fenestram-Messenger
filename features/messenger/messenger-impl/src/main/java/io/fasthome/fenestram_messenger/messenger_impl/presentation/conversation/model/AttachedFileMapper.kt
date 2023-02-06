package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import android.net.Uri
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UriLoadableContent

object AttachedFileMapper {
    fun List<Content>.toAttachedImages() = this.map { AttachedFile.Image(it) }
    fun List<AttachedFile.Image>.toContents() = this.map { it.content }
    fun List<AttachedFile.Image>.toContentUriList(): List<Uri> {
        var uriList = emptyList<Uri>()
        this.forEach {
            if(it.content is UriLoadableContent) {
                uriList = uriList + it.content.uri
            }
        }
        return uriList
    }

    fun List<AttachedFile.Document>.toFiles() = this.map { it.file }
}