package io.fasthome.fenestram_messenger.camera_impl.data.storage

import io.fasthome.fenestram_messenger.data.FileStorage
import java.io.File

class CameraFileStorage(
    fileStorageFactory: FileStorage.Factory,
) {
    private val fileStorage by fileStorageFactory.create("photos")

    private fun getPath(itemId: String) = "${File.separator}$itemId"

    suspend fun readFile(itemId: String): ByteArray? {
        val path = getPath(itemId)
        val fileExist = fileStorage.isFileExist(path)

        if (!fileExist) {
            return null
        }

        return fileStorage.readFile(path)
    }

    suspend fun saveFile(itemId: String, content: ByteArray) {
        val path = getPath(itemId)
        val fileExist = fileStorage.isFileExist(path)

        if (fileExist) {
            fileStorage.deleteFile(path)
        }

        fileStorage.saveFile(path, content)
    }

    suspend fun deleteFile(itemId: String) {
        fileStorage.deleteFile(getPath(itemId))
    }

    suspend fun clear() {
        fileStorage.clear()
    }

    suspend fun isFileExists(itemId: String): Boolean {
        val path = getPath(itemId)
        return fileStorage.isFileExist(path)
    }
}