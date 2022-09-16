package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import android.os.Parcelable
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import kotlinx.parcelize.Parcelize

object GroupGuestContract :
    NavigationContract<GroupGuestContract.Params, GroupGuestContract.Result>(GroupGuestBottomFragment::class) {
    @Parcelize
    data class Params(val participantsParams: List<ParticipantsViewItem>, val chatId: Long) : Parcelable

    sealed class Result : Parcelable {

        @Parcelize
        class UsersAdded(val users: List<ParticipantsViewItem>) : Result()

        @Parcelize
        object Canceled : Result()
    }
}