package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.databinding.ItemDocumentBinding
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.model.MetaInfo

class ConversationDocumentAdapter(
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
) : AsyncListDifferDelegationAdapter<MetaInfo>(
    AdapterUtil.diffUtilItemCallbackEquals(
        MetaInfo::url
    ), AdapterUtil.adapterDelegatesManager(
        createDocumentAdapterDelegate(onDownloadDocument = onDownloadDocument)
    )
)


fun createDocumentAdapterDelegate(onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit) =
    adapterDelegateViewBinding<MetaInfo, ItemDocumentBinding>(
        ItemDocumentBinding::inflate
    ) {

        bindWithBinding {
            renderDocument(
                metaInfo = item,
                progressBar = progressBar,
                fileName = fileName,
                fileSize = fileSize,
                startDownloadView = documentBg,
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
        if(!metaInfo.url.isNullOrEmpty())
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