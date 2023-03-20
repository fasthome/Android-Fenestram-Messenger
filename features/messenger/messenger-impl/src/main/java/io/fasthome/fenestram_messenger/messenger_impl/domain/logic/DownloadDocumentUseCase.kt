package io.fasthome.fenestram_messenger.messenger_impl.domain.logic

import android.Manifest
import android.os.Build
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.mvi.ScreenLauncher
import io.fasthome.fenestram_messenger.presentation.base.navigation.OpenFileNavigationContract
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.ProgressListener
import io.fasthome.fenestram_messenger.util.model.MetaInfo
import io.fasthome.fenestram_messenger.util.onSuccess

class DownloadDocumentUseCase(
    private val messengerInteractor: MessengerInteractor,
    private val copyDocumentToDownloadsUseCase: CopyDocumentToDownloadsUseCase
) {

    suspend fun invoke(
        documentLink: String,
        progressListener: ProgressListener,
        metaInfo: MetaInfo,

        permissionInterface: PermissionInterface,
        openFileLauncher: ScreenLauncher<OpenFileNavigationContract.Params>
    ) {
        messengerInteractor.getDocument(
            storagePath = documentLink,
            progressListener = progressListener
        ).onSuccess { loadedDocument ->
            val permissionGranted = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    || permissionInterface.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (!permissionGranted) return
            copyDocumentToDownloadsUseCase.invoke(
                loadedDocument.byteArray,
                PrintableText.Raw(documentLink),
                extension = metaInfo.extension
            ).onSuccess { uri ->
                openFileLauncher.launch(OpenFileNavigationContract.Params(uri))
            }
        }
    }
}