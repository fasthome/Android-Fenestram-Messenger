/**
 * Created by Dmitry Popov on 10.10.2022.
 */
package io.fasthome.fenestram_messenger.camera_api

import io.fasthome.fenestram_messenger.util.CallResult

interface FilesRepo {

    suspend fun getFile(itemId: String): CallResult<FileData?>

    suspend fun saveFile(
        itemId: String,
        content: ByteArray,
        fileName: String?,
    ): CallResult<Unit>

    suspend fun clearFiles()

}