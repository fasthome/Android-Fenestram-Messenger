package io.fasthome.fenestram_messenger.data.file

import android.content.Context
import io.fasthome.fenestram_messenger.data.FileStorage
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.CoroutineContext

internal class SimpleFileStorage private constructor(
    private val coroutineContext: CoroutineContext,
    private val baseDir: File,
) : FileStorage {

    open class Factory(private val dir: File) : FileStorage.Factory {
        override fun create(
            name: String,
            coroutineContext: CoroutineContext,
        ) = lazy {
            SimpleFileStorage(
                coroutineContext = coroutineContext,
                baseDir = File(dir, name)
                    .also { it.mkdirs() },
            )
        }
    }

    class FactoryFiles(context: Context) : Factory(context.filesDir)

    private fun getFile(path: String) = File(baseDir, path)
        .also { it.parentFile?.mkdirs() }

    override suspend fun isFileExist(path: String): Boolean = withContext(coroutineContext) {
        getFile(path).exists()
    }

    override suspend fun readFile(path: String): ByteArray = withContext(coroutineContext) {
        getFile(path).readBytes()
    }

    override suspend fun saveFile(path: String, content: ByteArray) {
        withContext(coroutineContext) {
            getFile(path).writeBytes(content)
        }
    }

    override suspend fun deleteFile(path: String) {
        withContext(coroutineContext) {
            getFile(path).deleteRecursively()
        }
    }

    override suspend fun clear() {
        withContext(coroutineContext) {
            baseDir.listFiles()?.forEach { it.deleteRecursively() }
        }
    }
}