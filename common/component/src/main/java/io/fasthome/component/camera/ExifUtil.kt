package io.fasthome.component.camera

import android.annotation.SuppressLint
import androidx.camera.core.impl.utils.Exif
import java.io.File

@SuppressLint("RestrictedApi")
internal object ExifUtil {

    fun getRotationDegrees(file: File): Int = Exif.createFromFile(file).rotation

    fun setInfo(
        file: File,
        addTimestamp: Boolean,
    ) {
        file.modifyExif {
            setTimestamp(addTimestamp)
        }
    }

    private fun Exif.setTimestamp(addTimestamp: Boolean) {
        if (addTimestamp) {
            attachTimestamp()
        } else {
            removeTimestamp()
        }
    }

    private fun File.modifyExif(action: Exif.() -> Unit) {
        val exif = Exif.createFromFile(this)
        exif.action()
        exif.save()
    }
}