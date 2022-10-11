package io.fasthome.component.camera

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.R
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.data.FileSystemInterface
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File

class CameraComponentViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
    private val params: CameraComponentParams,
    private val permissionInterface: PermissionInterface,
    private val fileSystemInterface: FileSystemInterface,
    private val cameraImageOperations: CameraImageOperations,
) : BaseViewModel<CameraComponentState, CameraComponentEvent>(router, requestParams),
    CameraComponentInterface {

    private val tempPhotoFile by lazy { File(fileSystemInterface.cacheDir, "temp_photo.jpg") }

    private val resultEventsChannel =
        Channel<CameraComponentInterface.ResultEvent>(Channel.UNLIMITED)

    init {
        viewModelScope.launch {
            if (!permissionInterface.request(Manifest.permission.CAMERA)) {
                resultEventsChannel.trySend(CameraComponentInterface.ResultEvent.CameraPermissionDenied).isSuccess
                return@launch
            }

            if (params.copyToExternalDir) {
                cameraImageOperations.prepareForCopyToExternalDir()
            }

            checkFreeSpaceOnDisk()
            updateState { it.copy(cameraType = CameraComponentState.CameraType.Back) }
        }
    }

    override fun createInitialState() = CameraComponentState(
        cameraType = null,
        rotation = CameraComponentState.Rotation.Rotation0,
        minResolution = params.minResolution,
        flash = CameraComponentState.Flash.Off,
        isFlashButtonEnabled = false,
        inProgress = false,
    )

    override fun stateUpdates(): StateFlow<CameraComponentState> = viewState

    override fun resultEvents(): Flow<CameraComponentInterface.ResultEvent> =
        resultEventsChannel.receiveAsFlow()

    fun onCameraNotFound() {
        switchCamera()
    }

    fun onCameraStart(hasFlash: Boolean) {
        updateState { it.copy(isFlashButtonEnabled = hasFlash) }
    }

    fun onDeviceRotated(degrees: Int) {
        updateState { it.copy(rotation = calcRotation(params.fixedOrientation, degrees)) }
    }

    override fun switchCamera() {
        updateState {
            val currentCamera = it.cameraType ?: return@updateState it
            val cameraValues = CameraComponentState.CameraType.values()
            it.copy(
                cameraType = cameraValues[(currentCamera.ordinal + 1) % cameraValues.size],
                flash = CameraComponentState.Flash.Off,
                isFlashButtonEnabled = false,
            )
        }
    }

    override fun switchFlash() {
        updateState {
            it.cameraType ?: return@updateState it
            val flashValues = CameraComponentState.Flash.values()
            it.copy(flash = flashValues[(it.flash.ordinal + 1) % flashValues.size])
        }
    }

    override fun capture() {
        currentViewState.cameraType ?: return
        sendEvent(CameraComponentEvent.Capture(file = tempPhotoFile))
    }

    fun onCaptured() {
        viewModelScope.launch {
            updateState { it.copy(cameraType = null, inProgress = true) }
            cameraImageOperations.processImage(
                imageFile = tempPhotoFile,
                maxPhotoSize = params.maxPhotoSize,
            )
            if (params.copyToExternalDir) {
                cameraImageOperations.copyPhotoToExternalDir(tempPhotoFile)
            }
            updateState { it.copy(inProgress = false) }
            resultEventsChannel.trySend(
                CameraComponentInterface.ResultEvent.PhotoCaptured(tempPhotoFile)
            ).isSuccess
        }
    }

    fun onCaptureError() {
        resultEventsChannel.trySend(CameraComponentInterface.ResultEvent.PhotoCaptureError).isSuccess
    }

    private fun checkFreeSpaceOnDisk() {
        val neededFreeSpaceOnDisk = params.neededFreeSpaceOnDisk ?: return
        if (fileSystemInterface.availableBytesOnDisk < neededFreeSpaceOnDisk) {
            showAlert(
                PrintableText.StringResource(
                    R.string.camera_need_free_space_message,
                    params.neededFreeSpaceOnDisk.megabytes(),
                )
            )
        }
    }

    companion object {
        fun calcRotation(
            fixedOrientation: CameraComponentParams.CameraOrientation?,
            degrees: Int,
        ): CameraComponentState.Rotation = when (fixedOrientation) {
            CameraComponentParams.CameraOrientation.Landscape -> when (degrees) {
                in 0..179 -> CameraComponentState.Rotation.Rotation270
                else -> CameraComponentState.Rotation.Rotation90
            }
            CameraComponentParams.CameraOrientation.Portrait -> CameraComponentState.Rotation.Rotation0
            null -> when (degrees) {
                in 45..134 -> CameraComponentState.Rotation.Rotation270
                in 135..224 -> CameraComponentState.Rotation.Rotation0
                in 225..314 -> CameraComponentState.Rotation.Rotation90
                else -> CameraComponentState.Rotation.Rotation0
            }
        }
    }
}