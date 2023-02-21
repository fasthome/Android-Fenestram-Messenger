package io.fasthome.fenestram_messenger.util.model

import android.os.Parcelable
import io.fasthome.fenestram_messenger.util.fileSizeInMb
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class MetaInfo(
    var name: String,
    /**
     * Расширение файла, напр. ".txt"
     */
    var extension: String,
    /**
     * Размер файла указанный в мегабайтах
     */
    var size: Float,
    var url: String?
) : Parcelable {
    constructor() : this("File", "", 0f, "")

    constructor(file: File) : this(
        file.name,
        file.extension,
        fileSizeInMb(file.length()),
        null
    )
}