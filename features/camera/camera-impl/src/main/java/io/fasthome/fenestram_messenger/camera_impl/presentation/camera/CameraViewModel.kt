package io.fasthome.fenestram_messenger.camera_impl.presentation.camera

import CameraNavigationContract
import androidx.lifecycle.viewModelScope
import io.fasthome.component.camera.CameraComponentInterface
import io.fasthome.fenestram_messenger.camera_api.CameraResult
import io.fasthome.fenestram_messenger.camera_impl.R
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CameraViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
    private val cameraComponentInterface: CameraComponentInterface,
) : BaseViewModel<CameraState, CameraEvent>(router, requestParams) {

    override fun createInitialState() = CameraState(
        cameraComponentState = cameraComponentInterface.stateUpdates().value,
    )

    init {
        cameraComponentInterface.stateUpdates()
            .onEach { cameraComponentState -> updateState { it.copy(cameraComponentState = cameraComponentState) } }
            .launchIn(viewModelScope)

        cameraComponentInterface.resultEvents()
            .onEach {
                when (it) {
                    CameraComponentInterface.ResultEvent.CameraPermissionDenied -> {
                        showPopup(PrintableText.StringResource(R.string.camera_permission_denied))
                        router.exit()
                    }
                    CameraComponentInterface.ResultEvent.PhotoCaptureError -> {
                        showPopup(PrintableText.StringResource(R.string.camera_capture_error))
                    }
                    is CameraComponentInterface.ResultEvent.PhotoCaptured -> {
                        exitWithResult(
                            CameraNavigationContract.createResult(
                                CameraResult(
                                    tempFile = it.tempFile,
                                )
                            )
                        )
                    }
                }
            }
            .launchIn(viewModelScope)

    }

    fun onCaptureClicked() {
        if (currentViewState.cameraComponentState.inProgress) return
        updateState { state -> state.copy(cameraComponentState = state.cameraComponentState.copy(inProgress = true)) }

        cameraComponentInterface.capture()
    }

    fun onSwitchCameraClicked() {
        if (currentViewState.cameraComponentState.inProgress) return

        cameraComponentInterface.switchCamera()
    }

    fun onSwitchFlashClicked() {
        if (currentViewState.cameraComponentState.inProgress) return

        cameraComponentInterface.switchFlash()
    }

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }
}