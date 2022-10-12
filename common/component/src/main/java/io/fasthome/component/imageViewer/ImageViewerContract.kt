package io.fasthome.component.imageViewer

import android.graphics.Bitmap
import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import kotlinx.parcelize.Parcelize

object ImageViewerContract : NavigationContract<ImageViewerContract.Params, NoResult>(ImageViewerFragment::class) {

    @Parcelize
    data class Params(
        val imageUrl : String?,
        val imageBitmap : Bitmap?
    ) : Parcelable

}