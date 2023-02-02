package io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model

import android.net.Uri
import android.os.Parcelable
import io.fasthome.fenestram_messenger.util.android.ByteArrayWrapper
import kotlinx.parcelize.Parcelize
import java.io.File

sealed interface Content : Parcelable {
    @Parcelize
    data class FileContent(val file: File) : Content


    /**
     * Для uri с схемой "content://"
     */
    @Parcelize
    data class GalleryContent(val galleryImageUri: Uri): Content

    interface LoadableContent : Content {
        suspend fun load(): ByteArrayWrapper?
    }
}