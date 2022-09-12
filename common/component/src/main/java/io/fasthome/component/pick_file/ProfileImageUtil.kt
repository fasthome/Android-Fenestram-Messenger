package io.fasthome.component.pick_file

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

interface ProfileImageUtil {
    suspend fun getPhoto(file: File): Bitmap?

    suspend fun getCroppedFile(bitmap: Bitmap, outputFile: File, paddings: Paddings)
}

class ProfileImageUtilImpl : ProfileImageUtil {
    override suspend fun getPhoto(file: File): Bitmap? = withContext(DispatchersProvider.IO) {
        return@withContext BitmapFactory.decodeFile(file.path)
    }

    override suspend fun getCroppedFile(bitmap: Bitmap, outputFile: File, paddings: Paddings) {
        withContext(Dispatchers.IO) {
            val cropped = cropByMask(
                input = bitmap,
                paddings = paddings,
            )

            outputFile.apply {
                writeBitmap(
                    bitmap = cropped,
                    format = Bitmap.CompressFormat.JPEG,
                    quality = 100,
                )
            }
        }
    }

    private fun cropByMask(input: Bitmap, paddings: Paddings): Bitmap =
        Bitmap.createBitmap(
            input,
            paddings.start,
            paddings.top,
            input.width - (paddings.start + paddings.end),
            input.height - (paddings.bottom + paddings.top)
        )

    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
        }
    }
}

