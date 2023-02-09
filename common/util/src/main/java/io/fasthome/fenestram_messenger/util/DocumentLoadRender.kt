package io.fasthome.fenestram_messenger.util

import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import io.fasthome.fenestram_messenger.util.model.MetaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias ProgressListener = suspend (progress : Int, loadedBytesSize : Long, fullBytesSize : Long, isReady : Boolean) -> Unit

suspend inline fun renderDownloadListener(
    progressBar: ProgressBar,
    progress: Int,
    metaInfo: MetaInfo?,
    fileSize: TextView,
    isReady: Boolean,
    loadedBytesSize: Long,
    fullBytesSize: Long,
) {
    withContext(Dispatchers.Main) {
        progressBar.isVisible = !isReady
        progressBar.progress = progress

        metaInfo?.let { meta ->
            if (isReady) {
                fileSize.setPrintableText(getPrettySize(fullBytesSize))
            } else {
                fileSize.setPrintableText(getLoadedFileSize(loadedBytesSize, fullBytesSize))
            }

        }
    }
}