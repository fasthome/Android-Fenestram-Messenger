package io.fasthome.component.camera

import android.os.Parcelable
import io.fasthome.fenestram_messenger.util.model.Bytes
import io.fasthome.fenestram_messenger.util.model.Size
import kotlinx.parcelize.Parcelize

@Parcelize
data class CameraComponentParams(
    val minResolution: Size = Size(3200, 2400),
    val maxPhotoSize: Bytes?,
    val fixedOrientation: CameraOrientation?,
    val neededFreeSpaceOnDisk: Bytes? = null,
    val copyToExternalDir: Boolean,
) : Parcelable {
    enum class CameraOrientation { Landscape, Portrait }

}