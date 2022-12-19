package io.fasthome.component.pick_file

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.util.android.getFileName
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

interface PickFileOperations {
    suspend fun copyFile(
        uri: Uri,
        tempFile: File,
    )

    fun renameFile(oldFile: File, destUri: Uri, destFileParent: File): File
}

class PickFileOperationsImpl(
    private val appContext: Context,
) : PickFileOperations {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun copyFile(uri: Uri, tempFile: File) {
        withContext(DispatchersProvider.IO) {
            appContext.contentResolver.openInputStream(uri)!!.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun renameFile(oldFile: File, destUri: Uri, destFileParent: File): File {
        val fileName = destUri.getFileName(appContext) ?: return oldFile
        val newFileName = "document_${
            SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(
                Date()
            )
        }${fileName.subSequence(fileName.indexOf('.'),fileName.length)}"

        val fileWithRealName = File(destFileParent, newFileName)
        return if (oldFile.renameTo(fileWithRealName)) {
            fileWithRealName
        } else {
            oldFile
        }
    }
}