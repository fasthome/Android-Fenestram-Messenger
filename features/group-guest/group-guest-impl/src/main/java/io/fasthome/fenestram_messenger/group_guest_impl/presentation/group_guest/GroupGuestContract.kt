package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import android.os.Parcelable
import io.fasthome.fenestram_messenger.group_guest_api.ParticipantsParams
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

object GroupGuestContract :
    NavigationContract<GroupGuestContract.Params, GroupGuestContract.Result>(GroupGuestBottomFragment::class) {
    @Parcelize
    data class Params(val participantsParams: ParticipantsParams) : Parcelable

    sealed class Result : Parcelable {

        @Parcelize
        class UsersAdded(val users: @RawValue List<ParticipantsViewItem>) : Result()

        @Parcelize
        object Canceled : Result()
    }
}