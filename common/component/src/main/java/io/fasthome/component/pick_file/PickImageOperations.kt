package io.fasthome.component.pick_file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
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

    suspend fun compressBitmap(
        bitmap: Bitmap,
        compressToSize: Bytes?,
        tempFile: File
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
                }.modifyOrientation(tempFile.absolutePath)

                FileOutputStream(tempFile).use { output ->
                    scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, output)
                }

                bitmap.recycle()
                scaledBitmap?.recycle()
            } else {
                tempFile.delete()
                tempFile.createNewFile()
            }
        }

    }
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun compressBitmap(
        bitmap: Bitmap,
        compressToSize: Bytes?,
        tempFile: File
    ) {
        withContext(DispatchersProvider.IO) {
            val scaledBitmap = if (compressToSize != null) {
                JpgUtil.downscale(
                    originalBitmap = bitmap,
                    downscaleParams = JpgUtil.DownscaleParams(maxSize = compressToSize),
                )
            } else {
                bitmap
            }.modifyOrientation(tempFile.absolutePath)

            FileOutputStream(tempFile).use { output ->
                scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, output)
            }

            bitmap.recycle()
            scaledBitmap?.recycle()
        }
    }

    /***
     * Изображения из галерии иногда могут быть перевернуты, поэтому необходимо это обрабатывать
     */
    private fun Bitmap.modifyOrientation(image_absolute_path: String?): Bitmap? {
        val ei = ExifInterface(image_absolute_path ?: return null)
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(this, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(this, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(this, 270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(this, horizontal = true, vertical = false)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(this, horizontal = false, vertical = true)
            else -> this
        }
    }

    private fun rotate(bitmap: Bitmap, degrees: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap? {
        val matrix = Matrix()
        matrix.preScale(if (horizontal) -1f else 1f, if (vertical) -1f else 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}