package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import android.os.Parcelable
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.group_guest_api.ParticipantsParams
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import kotlinx.parcelize.Parcelize

object GroupGuestContract :
    NavigationContract<GroupGuestContract.Params, GroupGuestContract.Result>(GroupGuestBottomFragment::class) {
    @Parcelize
    data class Params(val participantsParams: ParticipantsParams) : Parcelable

    sealed class Result : Parcelable {

        @Parcelize
        class UsersAdded(val users: List<User>) : Result()

        @Parcelize
        object Canceled : Result()
    }
}