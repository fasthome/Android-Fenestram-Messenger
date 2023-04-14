/**
 * Created by Dmitry Popov on 10.10.2022.
 */
package io.fasthome.fenestram_messenger.camera_impl.data.repo_impl

import io.fasthome.fenestram_messenger.camera_api.FileData
import io.fasthome.fenestram_messenger.camera_api.FilesRepo
import io.fasthome.fenestram_messenger.camera_impl.data.storage.CameraFileStorage
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.android.wrap
import io.fasthome.fenestram_messenger.util.callForResult
import java.io.File

class FilesRepoImpl(private val cameraFileStorage: CameraFileStorage) :
    FilesRepo {

    private fun getPath(itemId: String) = "${File.separator}$itemId"

    override suspend fun getFile(itemId: String): CallResult<FileData?> = callForResult {
        val byteArray = cameraFileStorage.readFile(getPath(itemId))

        FileData(byteArray?.wrap() ?: return@callForResult null)
    }

    override suspend fun saveFile(itemId: String, content: ByteArray, fileName: String?): CallResult<Unit> =
        callForResult {
            val path = getPath(itemId)
            val fileExist = cameraFileStorage.isFileExists(path)

            if (fileExist) {
                cameraFileStorage.deleteFile(path)
            }

            cameraFileStorage.saveFile(path, content)
        }

    override suspend fun clearFiles() = cameraFileStorage.clear()

}