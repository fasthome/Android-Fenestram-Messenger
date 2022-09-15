package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import android.os.Parcelable
import io.fasthome.fenestram_messenger.group_guest_api.ParticipantsParams
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import kotlinx.parcelize.Parcelize

object GroupGuestContract : NavigationContract<GroupGuestContract.Params, NoResult>(GroupGuestBottomFragment::class) {
    @Parcelize
    data class Params(val participantsParams: ParticipantsParams) : Parcelable
}