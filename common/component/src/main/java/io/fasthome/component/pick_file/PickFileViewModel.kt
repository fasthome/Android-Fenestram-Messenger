package io.fasthome.component.pick_file

import android.Manifest
import android.net.Uri
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class PickFileViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
    private val params: PickFileComponentParams,
    private val permissionInterface: PermissionInterface,
    private val fileSystemInterface: FileSystemInterface,
    private val pickImageOperations: PickImageOperations,
    private val pickFileOperations: PickFileOperations,
) : BaseViewModel<Unit, Nothing>(router, requestParams), PickFileInterface {

    private val cameraTempFile by lazy { createFile(fileSystemInterface.cacheDir, "picture") }

    private var currentMimeType = params.mimeType

    private val pickFileLauncher = registerScreen(PickFileNavigationContract) {
        val uri = it.uri
        if (uri == null) {
            resultEventsChannel.trySend(PickFileInterface.ResultEvent.PickCancelled)
        } else {
            viewModelScope.launch {
                val file by lazy { File(fileSystemInterface.cacheDir, "temp_file_" + UUID.randomUUID()) }
                when (currentMimeType) {
                    is PickFileComponentParams.MimeType.Image -> {
                        pickImageOperations.processImage(
                            uri = uri,
                            tempFile = file,
                            compressToSize = (currentMimeType as PickFileComponentParams.MimeType.Image).compressToSize,
                        )
                        resultEventsChannel.trySend(PickFileInterface.ResultEvent.PickedImage(file))
                    }
                    is PickFileComponentParams.MimeType.Document -> {
                        pickFileOperations.copyFile(uri, file)
                        val file = pickFileOperations.renameFile(file, uri, fileSystemInterface.cacheDir)
                        resultEventsChannel.trySend(PickFileInterface.ResultEvent.PickedFile(file))
                    }
                }
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
                resultEventsChannel.trySend(PickFileInterface.ResultEvent.PickedImage(cameraTempFile))
            }
        }
    }

    private val resultEventsChannel = Channel<PickFileInterface.ResultEvent>(Channel.UNLIMITED)

    override fun createInitialState() = Unit

    override fun resultEvents(): Flow<PickFileInterface.ResultEvent> =
        resultEventsChannel.receiveAsFlow()

    override fun processUri(uri: Uri) {
        viewModelScope.launch {
            val file by lazy { File(fileSystemInterface.cacheDir, "temp_file_" + UUID.randomUUID()) }
            pickImageOperations.processImage(
                uri = uri,
                tempFile = file,
                compressToSize = (currentMimeType as PickFileComponentParams.MimeType.Image).compressToSize,
            )
            resultEventsChannel.trySend(PickFileInterface.ResultEvent.PickedImage(file))
        }
    }

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