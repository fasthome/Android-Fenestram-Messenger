package io.fasthome.fenestram_messenger.messenger_impl.data

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File

interface DownloadFileManager {
    fun downloadFile(uri: String, fileName: String, callback: (path: String?) -> Unit)
}

class DownloadFileManagerImpl(val context: Context) : DownloadFileManager {
    override fun downloadFile(uri: String, fileName: String, callback: (path: String?) -> Unit) {
        try {
            if (!File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path + File.separator + fileName).exists()) {
                val downloadFileManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val link =
                    Uri.parse(uri)
                val request = DownloadManager.Request(link)

                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                    .setTitle(fileName)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                    .setDestinationInExternalFilesDir(
                        context,
                        Environment.DIRECTORY_DOWNLOADS,
                        File.separator + fileName
                    )
                downloadFileManager.enqueue(request)
            }
            callback(
                File.separator + fileName
            )
        } catch (e: Exception) {
            callback(null)
        }
    }
}