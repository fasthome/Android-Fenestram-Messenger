package io.fasthome.fenestram_messenger.util.android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix

object BitmapUtil {
    fun rotate(originalBitmap: Bitmap, degrees: Float): Bitmap = Bitmap.createBitmap(
        originalBitmap,
        0,
        0,
        originalBitmap.width,
        originalBitmap.height,
        Matrix().apply { postRotate(degrees) },
        true,
    )

    fun createBitmap(byteArray: ByteArray, dstWidth: Int, dstHeight: Int): Bitmap? {
        val bitmap = byteArray.createBitmap()
        return Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true)
    }
}

fun ByteArray.createBitmap(): Bitmap = BitmapFactory.decodeByteArray(this, 0, size)