package io.fasthome.fenestram_messenger.data

import android.content.Context
import android.os.StatFs
import io.fasthome.fenestram_messenger.util.model.Bytes
import java.io.File

interface FileSystemInterface {
    val availableBytesOnDisk: Bytes
    val cacheDir: File
}

class FileSystemInterfaceImpl(
    private val appContext: Context,
) : FileSystemInterface {

    override val availableBytesOnDisk: Bytes
        get() = StatFs(appContext.filesDir.absolutePath)
            .availableBytes
            .let(::Bytes)

    override val cacheDir: File get() = appContext.cacheDir
}