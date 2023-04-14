package io.fasthome.component.camera

import java.io.File

sealed interface CameraComponentEvent {

    data class Capture(val file: File) : CameraComponentEvent

}