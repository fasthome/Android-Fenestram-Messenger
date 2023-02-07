package io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector

import android.net.Uri
import io.fasthome.component.gallery.GalleryImage
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UriLoadableContent

object FileSelectorMapper {
    fun List<FileSelectorViewItem>.toContents() = this.map { it.content }
    fun List<UriLoadableContent>.toFileSelectorViewItem() = this.map { FileSelectorViewItem(content = it) }

    fun FileSelectorViewItem.toGalleryImage() = GalleryImage(
        uri = this.content.uri,
        cursorPosition = cursorPosition
    )

    fun GalleryImage.toViewItem(contentList: List<Uri> = emptyList()) = FileSelectorViewItem(
        content = UriLoadableContent(this.uri),
        cursorPosition = cursorPosition,
        isChecked = contentList.contains(this.uri)
    )
}