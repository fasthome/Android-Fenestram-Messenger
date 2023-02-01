package io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import io.fasthome.fenestram_messenger.util.android.ByteArrayWrapper
import io.fasthome.fenestram_messenger.util.android.wrap
import io.fasthome.fenestram_messenger.util.callForResult
import io.fasthome.fenestram_messenger.util.getOrNull
import io.fasthome.fenestram_messenger.util.readBytes
import kotlinx.parcelize.Parcelize
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.io.File

sealed interface Content : Parcelable {
    @Parcelize
    data class FileContent(val file: File) : Content

    interface LoadableContent : Content {
        suspend fun load(): ByteArrayWrapper?
    }
}

@Parcelize
class UriLoadableContent(
    private val uri: Uri
) : Content.LoadableContent, KoinComponent {

    override suspend fun load(): ByteArrayWrapper? {
        val bytes = callForResult { readBytes(get<Context>().contentResolver, uri) }.getOrNull()
        return bytes?.wrap()
    }
}