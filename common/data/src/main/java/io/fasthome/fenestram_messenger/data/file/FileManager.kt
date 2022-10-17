package io.fasthome.fenestram_messenger.data.file

import android.Manifest
import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.content.FileProvider
import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.core.time.TimeProvider
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getPrintableText
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

interface FileManager {
    suspend fun copyFileToDownloads(
        byteArray: ByteArray,
        fileName: PrintableText,
        extension: String,
        mimeType: String
    ): Uri
}

internal class FileManagerImpl(
    private val appContext: Context,
    private val timeProvider: TimeProvider,
) : FileManager {

    private val contentResolver = appContext.contentResolver

    override suspend fun copyFileToDownloads(
        byteArray: ByteArray,
        fileName: PrintableText,
        extension: String,
        mimeType: String
    ): Uri = withContext(DispatchersProvider.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            copyByteArrayToDownloadsQ(byteArray, fileName, mimeType)
        } else {
            copyByteArrayToDownloadsLegacy(byteArray, fileName, extension)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun copyByteArrayToDownloadsQ(byteArray: ByteArray, fileName: PrintableText, mimeType: String): Uri {
        val now = timeProvider.currentTimeMillis
        val fileNameValue = appContext.getPrintableText(fileName)

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.TITLE, fileNameValue)
            put(MediaStore.Downloads.DISPLAY_NAME, fileNameValue)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.DATE_ADDED, now)
            put(MediaStore.Downloads.DATE_MODIFIED, now)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)!!

        contentResolver.openFileDescriptor(uri, "w")!!.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { output ->
                byteArray.inputStream().use { input ->
                    input.copyTo(output)
                }
            }
        }

        contentValues.clear()
        contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
        contentResolver.update(uri, contentValues, null, null)
        return uri
    }

    @TargetApi(Build.VERSION_CODES.P)
    @Suppress("DEPRECATION")
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, conditional = true)
    private fun copyByteArrayToDownloadsLegacy(
        byteArray: ByteArray,
        fileName: PrintableText,
        extension: String,
    ): Uri {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        downloadDir.mkdirs()

        val fileNameValue = appContext.getPrintableText(fileName)

        var index = 0
        var file = File(downloadDir, "$fileNameValue.$extension")
        while (file.exists()) {
            ++index
            file = File(downloadDir, "$fileNameValue($index).$extension")
        }
        file.writeBytes(byteArray)

        return FileProvider.getUriForFile(appContext, appContext.packageName + ".provider", file)
    }
}