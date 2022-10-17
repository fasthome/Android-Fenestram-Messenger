package io.fasthome.component.pick_file

import android.os.Parcelable
import io.fasthome.fenestram_messenger.util.model.Bytes
import kotlinx.parcelize.Parcelize

@Parcelize
data class PickFileComponentParams(
    val mimeType: MimeType,
) : Parcelable {

    sealed class MimeType : Parcelable {
        abstract val value: String

        @Parcelize
        data class Image(
            override val value: String = "image/*",
            val compressToSize: Bytes?
        ) : MimeType()

        @Parcelize
        data class Document(override val value: String = "application/*") : MimeType()
    }
}