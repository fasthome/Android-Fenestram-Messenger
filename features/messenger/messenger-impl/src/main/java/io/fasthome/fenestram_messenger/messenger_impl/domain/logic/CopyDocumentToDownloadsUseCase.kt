package io.fasthome.fenestram_messenger.messenger_impl.domain.logic

import android.net.Uri
import io.fasthome.fenestram_messenger.data.file.FileManager
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.callForResult

class CopyDocumentToDownloadsUseCase(
    private val fileManager: FileManager,
) {
    suspend fun invoke(byteArray: ByteArray, fileName: PrintableText, extension : String): CallResult<Uri> = callForResult {
        val mimeType = "application/$extension"

        fileManager.copyFileToDownloads(byteArray, fileName, extension, mimeType)
    }
}