package io.fasthome.component.pick_file

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.data.FileSystemInterface
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.presentation.base.navigation.PickFileNavigationContract
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File

class PickFileViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
    private val params: PickFileComponentParams,
    private val permissionInterface : PermissionInterface,
    private val fileSystemInterface: FileSystemInterface,
    private val pickImageOperations: PickImageOperations,
    private val pickFileOperations: PickFileOperations,
) : BaseViewModel<Unit, Nothing>(router, requestParams), PickFileInterface {

    private val tempFile by lazy { File(fileSystemInterface.cacheDir, "temp_file") }

    private val pickFileLauncher = registerScreen(PickFileNavigationContract) {
        val uri = it.uri
        if (uri == null) {
            resultEventsChannel.trySend(PickFileInterface.ResultEvent.PickCancelled)
        } else {
            viewModelScope.launch {
                val file = when (val mimeType = params.mimeType) {
                    is PickFileComponentParams.MimeType.Image -> {
                        pickImageOperations.processImage(
                            uri = uri,
                            tempFile = tempFile,
                            compressToSize = mimeType.compressToSize,
                        )
                        tempFile
                    }
                    is PickFileComponentParams.MimeType.Pdf -> {
                        pickFileOperations.copyFile(uri, tempFile)
                        pickFileOperations.renameFile(tempFile, uri, fileSystemInterface.cacheDir)
                    }
                }

                resultEventsChannel.trySend(PickFileInterface.ResultEvent.Picked(file))
            }
        }
    }

    private val resultEventsChannel = Channel<PickFileInterface.ResultEvent>(Channel.UNLIMITED)

    override fun createInitialState() = Unit

    override fun resultEvents(): Flow<PickFileInterface.ResultEvent> =
        resultEventsChannel.receiveAsFlow()

    override fun pickFile() {
        viewModelScope.launch {
            val permissionGranted =
                permissionInterface.request(Manifest.permission.READ_EXTERNAL_STORAGE, canOpenSettings = true)
            if (permissionGranted) {
                pickFileLauncher.launch(PickFileNavigationContract.Params(params.mimeType.value))
            }
        }
    }
}