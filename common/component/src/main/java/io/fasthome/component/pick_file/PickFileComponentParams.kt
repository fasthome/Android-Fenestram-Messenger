package io.fasthome.component.pick_file

import android.os.Parcelable
import io.fasthome.fenestram_messenger.util.model.Bytes
import kotlinx.parcelize.Parcelize

@Parcelize
data class PickFileComponentParams(
    val mimeType: MimeType,
) : Parcelable {

    sealed class MimeType : Parcelable {
        abstract val value: List<String>

        @Parcelize
        data class Image(
            override val value: List<String> = listOf("image/*"),
            val compressToSize: Bytes?
        ) : MimeType()

        @Parcelize
        data class Document(override val value: List<String> = listOf(DOC, DOCX, TXT, PDF, PPT, PPTX, XLS, XLSX, RTF, HTML, ODT)) : MimeType()

        companion object {
            const val DOC = "application/msword"
            const val DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            const val TXT = "text/plain"
            const val PDF = "application/pdf"
            const val PPT = "application/vnd.ms-powerpoint"
            const val PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            const val XLS = "application/vnd.ms-excel"
            const val XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            const val RTF = "application/rtf"
            const val HTML = "text/html"
            const val ODT = "application/vnd.oasis.opendocument.text"
        }
    }
}