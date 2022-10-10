package io.fasthome.component.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.core.time.TimeProvider
import io.fasthome.fenestram_messenger.util.JpgUtil
import io.fasthome.fenestram_messenger.util.android.BitmapUtil
import io.fasthome.fenestram_messenger.util.model.Bytes
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter

interface CameraImageOperations {

    suspend fun prepareForCopyToExternalDir()

    suspend fun processImage(
        imageFile: File,
        maxPhotoSize: Bytes?,
    )

    suspend fun copyPhotoToExternalDir(internalFile: File)
}

class CameraImageOperationsImpl(
    appContext: Context,
    private val timeProvider: TimeProvider,
    private val permissionInterface: PermissionInterface,
) : CameraImageOperations {

    companion object {
        private const val PHOTOS_DIR_NAME = "HooliChat"
        private const val PHOTO_EXTENSION = "jpg"
    }

    private val contentResolver = appContext.contentResolver

    override suspend fun prepareForCopyToExternalDir() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionInterface.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, canOpenSettings = false)
        }
    }


    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun processImage(
        imageFile: File,
        maxPhotoSize: Bytes?,
    ) {
        withContext(DispatchersProvider.IO) {
            val rotationDegrees = ExifUtil.getRotationDegrees(imageFile).toFloat()

            val originalBitmap = BitmapFactory.decodeFile(imageFile.path)
            val scaledBitmap: Bitmap = if (maxPhotoSize != null) {
                JpgUtil.downscale(
                    originalBitmap = originalBitmap,
                    downscaleParams = JpgUtil.DownscaleParams(maxSize = maxPhotoSize),
                )
            } else {
                originalBitmap
            }

            if (scaledBitmap != originalBitmap) originalBitmap.recycle()

            val rotatedBitmap = BitmapUtil.rotate(scaledBitmap, rotationDegrees)

            if (rotatedBitmap != scaledBitmap) scaledBitmap.recycle()

            FileOutputStream(imageFile).use { output ->
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
            }

            rotatedBitmap.recycle()

            ExifUtil.setInfo(
                file = imageFile,
                addTimestamp = true,
            )
        }
    }


    override suspend fun copyPhotoToExternalDir(internalFile: File) {
        withContext(DispatchersProvider.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    copyPhotoToExternalDirQ(internalFile)
                } else {
                    copyPhotoToExternalDirLegacy(internalFile)
                }
            } catch (t: Throwable) {

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun copyPhotoToExternalDirQ(internalFile: File) {
        val externalFileName = "${getNowFormatted()}.$PHOTO_EXTENSION"

        val relativePath = "${Environment.DIRECTORY_DCIM}${File.separator}$PHOTOS_DIR_NAME"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, externalFileName)
            put(MediaStore.Images.ImageColumns.RELATIVE_PATH, relativePath)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val photosCollection =
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val photoUri = contentResolver.insert(photosCollection, contentValues)!!
        contentResolver.openFileDescriptor(photoUri, "w")!!.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { output ->
                FileInputStream(internalFile).use { input ->
                    input.copyTo(output)
                }
            }
        }

        contentValues.clear()
        contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
        contentResolver.update(photoUri, contentValues, null, null)
    }

    @Suppress("DEPRECATION")
    private fun copyPhotoToExternalDirLegacy(internalFile: File) {
        if (!permissionInterface.has(Manifest.permission.WRITE_EXTERNAL_STORAGE)) return

        val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val photosDir = File(dcimDir, PHOTOS_DIR_NAME)
        photosDir.mkdirs()
        val nowFormatted = getNowFormatted()
        var index = 0
        var file = File(photosDir, "$nowFormatted.$PHOTO_EXTENSION")
        while (file.exists()) {
            ++index
            file = File(photosDir, "$nowFormatted ($index).$PHOTO_EXTENSION")
        }
        file.writeBytes(internalFile.readBytes())
    }

    private fun getNowFormatted(): String = timeProvider.now()
        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"))
}