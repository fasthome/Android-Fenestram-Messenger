/**
 * Created by Dmitry Popov on 10.10.2022.
 */
package io.fasthome.fenestram_messenger.camera_impl

import CameraNavigationContract
import io.fasthome.fenestram_messenger.camera_api.CameraFeature
import io.fasthome.fenestram_messenger.camera_impl.presentation.confirm.ConfirmPhotoNavigationContract

class CameraFeatureImpl : CameraFeature {
    override val cameraNavigationContract = CameraNavigationContract
    override val confirmNavigationContract = ConfirmPhotoNavigationContract
}