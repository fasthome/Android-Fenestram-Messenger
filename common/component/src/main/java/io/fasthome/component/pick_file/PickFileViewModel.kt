package io.fasthome.component.pick_file

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.data.FileSystemInterface
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.presentation.base.navigation.CameraNavigationContract
import io.fasthome.fenestram_messenger.presentation.base.navigation.PickFileNavigationContract
import io.fasthome.fenestram_messenger.util.createFile
import io.fasthome.fenestram_messenger.util.model.Bytes
import java.io.File
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PickFileViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
    private val params: PickFileComponentParams,
    private val permissionInterface: PermissionInterface,
    private val fileSystemInterface: FileSystemInterface,
    private val pickImageOperations: PickImageOperations,
    private val pickFileOperations: PickFileOperations,
) : BaseViewModel<Unit, Nothing>(router, requestParams), PickFileInterface {

    private val tempFile by lazy { File(fileSystemInterface.cacheDir, "temp_file") }
    private val cameraTempFile by lazy { createFile(fileSystemInterface.cacheDir, "picture") }

    private var currentMimeType = params.mimeType

    private val pickFileLauncher = registerScreen(PickFileNavigationContract) {
        val uri = it.uri
        if (uri == null) {
            resultEventsChannel.trySend(PickFileInterface.ResultEvent.PickCancelled)
        } else {
            viewModelScope.launch {
                val file = when (currentMimeType) {
                    is PickFileComponentParams.MimeType.Image -> {
                        pickImageOperations.processImage(
                            uri = uri,
                            tempFile = tempFile,
                            compressToSize = (currentMimeType as PickFileComponentParams.MimeType.Image).compressToSize,
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

    private val cameraLauncher = registerScreen(CameraNavigationContract) {
        if (it.bitmap == null) {
            resultEventsChannel.trySend(PickFileInterface.ResultEvent.PickCancelled)
        } else {
            viewModelScope.launch {
                pickImageOperations.compressBitmap(
                    bitmap = it.bitmap!!,
                    tempFile = cameraTempFile,
                    compressToSize = Bytes(
                        Bytes.BYTES_PER_MB
                    ),
                )
                resultEventsChannel.trySend(PickFileInterface.ResultEvent.Picked(cameraTempFile))
            }
        }
    }

    private val resultEventsChannel = Channel<PickFileInterface.ResultEvent>(Channel.UNLIMITED)

    override fun createInitialState() = Unit

    override fun resultEvents(): Flow<PickFileInterface.ResultEvent> =
        resultEventsChannel.receiveAsFlow()

    override fun pickFile(mimeType: PickFileComponentParams.MimeType?) {
        viewModelScope.launch {
            val permissionGranted =
                permissionInterface.request(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    canOpenSettings = true
                )
            if (permissionGranted) {
                currentMimeType = if (mimeType != null)
                    mimeType
                else
                    params.mimeType

                pickFileLauncher.launch(PickFileNavigationContract.Params(currentMimeType.value))
            }
        }
    }

    override fun launchCamera() {
        viewModelScope.launch {
            val permissionGranted =
                permissionInterface.request(
                    Manifest.permission.CAMERA,
                    canOpenSettings = true
                )
            if (permissionGranted) {
                cameraLauncher.launch()
            }
        }
    }
}