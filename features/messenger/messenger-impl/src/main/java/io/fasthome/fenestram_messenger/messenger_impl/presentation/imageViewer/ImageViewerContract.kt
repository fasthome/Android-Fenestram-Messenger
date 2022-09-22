package io.fasthome.fenestram_messenger.messenger_impl.presentation.imageViewer

import android.os.Parcelable
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.MessengerFragment
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import kotlinx.parcelize.Parcelize

object ImageViewerContract : NavigationContract<ImageViewerContract.Params, NoResult>(ImageViewerFragment::class) {

    @Parcelize
    data class Params(
        val imageUrl : String
    ) : Parcelable

}