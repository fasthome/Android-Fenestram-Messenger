package io.fasthome.fenestram_messenger.util

import android.graphics.Bitmap
import io.fasthome.fenestram_messenger.util.model.Bytes
import java.io.ByteArrayOutputStream

object JpgUtil {

    data class DownscaleParams(
        val maxSize: Bytes,
        val stepScale: Double = 0.95,
        val quality: Int = 100,
    ) {
        init {
            require(0.0 < stepScale && stepScale < 1.0)
        }
    }

    fun downscale(
        originalBitmap: Bitmap,
        downscaleParams: DownscaleParams,
    ): Bitmap {
        val byteArrayOutputStream = ByteArrayOutputStream(originalBitmap.byteCount)
        originalBitmap.compress(
            Bitmap.CompressFormat.JPEG,
            downscaleParams.quality,
            byteArrayOutputStream,
        )
        if (byteArrayOutputStream.size() < downscaleParams.maxSize.bytes) {
            byteArrayOutputStream.reset()
            return originalBitmap
        }

        var bitmap = originalBitmap

        do {
            byteArrayOutputStream.reset()

            val scaledBitmap = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * downscaleParams.stepScale).toInt(),
                (bitmap.height * downscaleParams.stepScale).toInt(),
                true,
            )

            if (scaledBitmap != bitmap) bitmap.recycle()
            bitmap = scaledBitmap

            scaledBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                downscaleParams.quality,
                byteArrayOutputStream,
            )
        } while (byteArrayOutputStream.size() > downscaleParams.maxSize.bytes)

        return bitmap
    }
}