package io.fasthome.component.pick_file

import android.content.Context
import android.net.Uri
import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.util.android.getFileName
import kotlinx.coroutines.withContext
import java.io.File

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

    override fun renameFile(oldFile: File, destUri: Uri, destFileParent: File): File {
        val fileName = destUri.getFileName(appContext) ?: return oldFile

        val fileWithRealName = File(destFileParent, fileName)
        return if (oldFile.renameTo(fileWithRealName)) {
            fileWithRealName
        } else {
            oldFile
        }
    }
}