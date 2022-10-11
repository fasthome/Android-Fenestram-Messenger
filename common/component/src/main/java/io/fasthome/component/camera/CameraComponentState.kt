package io.fasthome.component.camera

import io.fasthome.fenestram_messenger.util.model.Size


data class CameraComponentState(
    val cameraType: CameraType?,
    val rotation: Rotation,
    val minResolution: Size,
    val flash: Flash,
    val isFlashButtonEnabled: Boolean,
    val inProgress: Boolean,
) {
    enum class CameraType { Back, Front }

    enum class Flash { Off, On, Auto, Torch }

    enum class Rotation(val degrees: Int, val isLandscape: Boolean) {
        Rotation0(degrees = 0, isLandscape = false),
        Rotation90(degrees = 90, isLandscape = true),
        Rotation270(degrees = 270, isLandscape = true),
    }
}