package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem
import kotlinx.parcelize.Parcelize

object ProfileGuestImagesNavigationContract :
    NavigationContract<ProfileGuestImagesNavigationContract.Params, NoResult>(ProfileGuestImagesFragment::class) {

        @Parcelize
        data class Params(
            val images : List<FileItem>
        ) : Parcelable

    }