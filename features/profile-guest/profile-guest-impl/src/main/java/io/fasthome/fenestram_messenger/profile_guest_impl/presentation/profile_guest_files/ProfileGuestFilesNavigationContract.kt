package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem
import kotlinx.parcelize.Parcelize

object ProfileGuestFilesNavigationContract :
    NavigationContract<ProfileGuestFilesNavigationContract.Params, NoResult>(
        ProfileGuestFilesFragment::class
    ) {

    @Parcelize
    data class Params(
        val docs: List<FileItem>
    ) : Parcelable

}