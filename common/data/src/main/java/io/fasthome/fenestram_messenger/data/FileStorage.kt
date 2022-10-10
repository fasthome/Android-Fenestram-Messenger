package io.fasthome.fenestram_messenger.data

import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import kotlin.coroutines.CoroutineContext

interface FileStorage {

    interface Factory {
        fun create(
            name: String,
            coroutineContext: CoroutineContext = DispatchersProvider.IO,
        ): Lazy<FileStorage>
    }

    suspend fun isFileExist(path: String): Boolean

    suspend fun readFile(path: String): ByteArray

    suspend fun saveFile(path: String, content: ByteArray)

    suspend fun deleteFile(path: String)

    suspend fun clear()
}