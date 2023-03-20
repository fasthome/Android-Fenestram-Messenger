package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UriLoadableContent

object AttachedFileMapper {
    fun List<Content>.toAttachedImages() = this.map { AttachedFile.Image(it) }
    fun List<AttachedFile.Image>.toContents() = this.map { it.content }

    fun List<AttachedFile.Image>.toContentUriList(): List<UriLoadableContent> {
        var uriList = emptyList<UriLoadableContent>()
        this.forEach {
            if(it.content is UriLoadableContent) {
                uriList = uriList + it.content
            }
        }
        return uriList
    }

    fun List<AttachedFile.Document>.toFiles() = this.map { it.file }
}