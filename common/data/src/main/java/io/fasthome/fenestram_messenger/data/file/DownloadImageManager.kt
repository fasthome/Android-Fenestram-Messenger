package io.fasthome.fenestram_messenger.data.file

import android.content.Context
import android.graphics.Bitmap.CompressFormat
import io.fasthome.fenestram_messenger.core.ui.extensions.loadOriginalBitmap
import io.fasthome.fenestram_messenger.core.ui.extensions.loadOriginalGif
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.callForResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

interface DownloadImageManager {
    suspend fun downloadImage(
        imageUrl: String
    ): CallResult<Unit>
}

class DownloadImageManagerImpl(
    private val context: Context,
    private val fileManager: FileManager,
) : DownloadImageManager {

    private var byteArray: ByteArray? = null

    override suspend fun downloadImage(imageUrl: String): CallResult<Unit> = callForResult {
        withContext(Dispatchers.IO) {
            val extension = imageUrl.substringAfterLast('.')

            when (extension) {
                "jpg", "jpeg" -> downloadBitmap(imageUrl, CompressFormat.JPEG)
                "png" -> downloadBitmap(imageUrl, CompressFormat.PNG)
                "gif" -> downloadGif(imageUrl)
                else -> {
                    throw Exception()
                }
            }

            fileManager.copyFileToDownloads(
                byteArray = byteArray ?: throw Exception(),
                fileName = PrintableText.Raw(imageUrl.substringAfterLast('/').substringBeforeLast('.')),
                extension = extension,
                mimeType = "image/$extension"
            )
        }
    }

    private fun downloadBitmap(imageUrl: String, compressFormat: CompressFormat) {
        ByteArrayOutputStream().use { stream ->
            val imageBitmap = loadOriginalBitmap(context, imageUrl)
            imageBitmap.compress(compressFormat, 100, stream)
            byteArray = stream.toByteArray()
        }
    }

    private fun downloadGif(imageUrl: String) {
        ByteArrayOutputStream().use { stream ->
            val imageGifBuffer = loadOriginalGif(context, imageUrl)
            byteArray = ByteArray(imageGifBuffer.capacity())
            (imageGifBuffer.duplicate().clear() as ByteBuffer).get(byteArray!!)
            stream.write(byteArray!!, 0, byteArray!!.size)
        }
    }
}