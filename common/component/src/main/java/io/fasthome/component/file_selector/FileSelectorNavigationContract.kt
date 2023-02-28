/**
 * Created by Dmitry Popov on 02.02.2023.
 */
package io.fasthome.component.file_selector

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UriLoadableContent
import kotlinx.parcelize.Parcelize

object FileSelectorNavigationContract :
    NavigationContract<FileSelectorNavigationContract.Params, FileSelectorNavigationContract.Result>(
        FileSelectorBottomFragment::class) {
    @Parcelize
    class Params(
        val selectedImages: List<UriLoadableContent>,
        val maxImagesCount: Int = IMAGES_COUNT_INFINITY,
        val canSelectFiles: Boolean = true
    ) : Parcelable {
        companion object {
            const val IMAGES_COUNT_INFINITY = -1
            const val IMAGES_COUNT_ONE = 1
        }
    }

    sealed class Result : Parcelable {

        @Parcelize
        class Attach(
            val images: List<Content>,
        ) : Result()

        @Parcelize
        object OpenGallery : Result()

        @Parcelize
        object OpenCamera : Result()

        @Parcelize
        object OpenFiles : Result()
    }

}