/**
 * Created by Dmitry Popov on 10.10.2022.
 */
package io.fasthome.fenestram_messenger.camera_api

import android.os.Parcelable
import io.fasthome.component.camera.CameraComponentParams
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.parcelize.Parcelize
import java.io.File

interface CameraFeature {

    val cameraNavigationContract: NavigationContractApi<CameraParams, CameraResult>

    val confirmNavigationContract: NavigationContractApi<ConfirmParams, ConfirmResult>

    suspend fun clearFileStorage()

    suspend fun saveFile(id: String, tempFile: File) : CallResult<Unit>


}

@Parcelize
data class CameraParams(
    val cameraComponentParams: CameraComponentParams,
) : Parcelable

@Parcelize
data class CameraResult(
    val tempFile: File,
) : Parcelable

@Parcelize
data class ConfirmParams(
    val content: Content,
) : Parcelable

@Parcelize
data class ConfirmResult(
    val action: Action,
) : Parcelable {
    sealed class Action : Parcelable {

        @Parcelize
        data class Confirm(val tempFile: File) : Action()

        @Parcelize
        object Retake : Action()

        @Parcelize
        object Cancel : Action()
    }
}
