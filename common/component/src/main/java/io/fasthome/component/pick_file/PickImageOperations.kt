package io.fasthome.component.pick_file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.util.JpgUtil
import io.fasthome.fenestram_messenger.util.model.Bytes
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

interface PickImageOperations {

    suspend fun processImage(
        uri: Uri,
        tempFile: File,
        compressToSize: Bytes?,
    )
}

class PickImageOperationsImpl(
    appContext: Context,
) : PickImageOperations {

    private val contentResolver = appContext.contentResolver

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun processImage(
        uri: Uri,
        tempFile: File,
        compressToSize: Bytes?,
    ) {
        withContext(DispatchersProvider.IO) {
            val bitmap = contentResolver.openInputStream(uri)!!.use { input ->
                BitmapFactory.decodeStream(input)
            }

            if (bitmap != null) {

                val scaledBitmap = if (compressToSize != null) {
                    JpgUtil.downscale(
                        originalBitmap = bitmap,
                        downscaleParams = JpgUtil.DownscaleParams(maxSize = compressToSize),
                    )
                } else {
                    bitmap
                }

                FileOutputStream(tempFile).use { output ->
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
                }

                bitmap.recycle()
                scaledBitmap.recycle()
            }
            else {
                tempFile.delete()
                tempFile.createNewFile()
            }
        }

    }
}