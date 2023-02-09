package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.RecentFileItemBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentFilesViewItem
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.model.MetaInfo

class RecentFilesAdapter(onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit) : AsyncListDifferDelegationAdapter<RecentFilesViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createFilesAdapterDelegate(onDownloadDocument)
    )
) {}

fun createFilesAdapterDelegate(onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit) =
    adapterDelegateViewBinding<RecentFilesViewItem, RecentFileItemBinding>(
        RecentFileItemBinding::inflate,
    ) {
        bindWithBinding {
            renderDocument(
                metaInfo = item.metaInfo,
                progressBar = progressBar,
                fileName = fileName,
                fileSize = fileSize,
                startDownloadView = root,
                onDownloadDocument = onDownloadDocument
            )
        }
    }

@SuppressLint("SuspiciousIndentation")
private fun renderDocument(
    //поле для клика скачивания
    startDownloadView: View? = null,

    //поля для скрытия view при отображении документа
    goneViews: List<View>? = null,

    //мета документа
    metaInfo: MetaInfo,

    progressBar: ProgressBar,
    fileName: TextView,
    fileSize: TextView,

    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
) {
    goneViews?.forEach {
        it.isVisible = false
    }

    fileSize.setPrintableText(getPrettySize(metaInfo.size))
    fileName.text = metaInfo.name

    progressBar.isVisible = false
    fileSize.isVisible = true

    val documentLoadClickListener = {
        if (!metaInfo.url.isNullOrEmpty())
            onDownloadDocument(metaInfo) { progress, loadedBytesSize, fullBytesSize, isReady ->
                renderDownloadListener(
                    progressBar = progressBar,
                    progress = progress,
                    loadedBytesSize = loadedBytesSize,
                    fullBytesSize = fullBytesSize,
                    metaInfo = metaInfo,
                    fileSize = fileSize,
                    isReady = isReady
                )
            }
    }
    startDownloadView?.onClick(documentLoadClickListener)
}