package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_edit

import android.os.Parcelable
import io.fasthome.fenestram_messenger.group_guest_api.ParticipantsParams
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import kotlinx.parcelize.Parcelize

object ProfileGuestEditNavigationContract :
    NavigationContract<ProfileGuestEditNavigationContract.Params, ProfileGuestEditNavigationContract.Result>(
        ProfileGuestEditFragment::class
    ) {

    @Parcelize
    data class Params(
        val id: Long,
        val userName: String,
        val userAvatar: String,
        val participantsCount: Int
    ) : Parcelable

    sealed class Result : Parcelable {

        @Parcelize
        class ChatDeleted(val id: Long) : Result()

        @Parcelize
        class ChatEdited(val newName: String, val newAvatar: String) : Result()

        @Parcelize
        object Canceled : Result()
    }
}