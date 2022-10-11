package io.fasthome.component.camera

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface CameraComponentInterface {

    sealed interface ResultEvent {
        object CameraPermissionDenied : ResultEvent
        object PhotoCaptureError : ResultEvent
        data class PhotoCaptured(val tempFile: File) : ResultEvent
    }

    fun switchCamera()
    fun switchFlash()
    fun capture()

    fun stateUpdates(): StateFlow<CameraComponentState>
    fun resultEvents(): Flow<ResultEvent>
}