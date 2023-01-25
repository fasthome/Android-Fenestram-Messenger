/**
 * Created by Dmitry Popov on 10.10.2022.
 */
package io.fasthome.fenestram_messenger.camera_impl

import CameraNavigationContract
import io.fasthome.fenestram_messenger.camera_api.CameraFeature
import io.fasthome.fenestram_messenger.camera_api.FilesRepo
import io.fasthome.fenestram_messenger.camera_impl.presentation.confirm.ConfirmPhotoNavigationContract
import java.io.File

class CameraFeatureImpl(private val filesRepo: FilesRepo) : CameraFeature {

    override val cameraNavigationContract = CameraNavigationContract

    override val confirmNavigationContract = ConfirmPhotoNavigationContract

    override suspend fun clearFileStorage() = filesRepo.clearFiles()

    override suspend fun saveFile(id: String, tempFile: File) = filesRepo.saveFile(itemId = id, content = tempFile.readBytes(), fileName = tempFile.name)

}