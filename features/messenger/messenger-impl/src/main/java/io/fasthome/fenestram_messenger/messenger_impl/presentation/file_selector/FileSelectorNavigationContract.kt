/**
 * Created by Dmitry Popov on 02.02.2023.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import kotlinx.parcelize.Parcelize

object FileSelectorNavigationContract :
    NavigationContract<NoParams, FileSelectorNavigationContract.Result>(FileSelectorBottomFragment::class) {

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