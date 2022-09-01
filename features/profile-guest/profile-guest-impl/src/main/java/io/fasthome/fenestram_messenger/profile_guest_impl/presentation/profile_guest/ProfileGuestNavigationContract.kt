package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import android.os.Parcelable
import io.fasthome.fenestram_messenger.group_guest_api.ParticipantsParams
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import kotlinx.parcelize.Parcelize

object ProfileGuestNavigationContract :
    NavigationContract<ProfileGuestNavigationContract.Params, NoResult>(ProfileGuestBottomFragment::class) {

        @Parcelize
        data class Params(
            val userName : String,
            val userNickname : String,
            val userAvatar : String,
            val isGroup : Boolean,
            val groupParticipantsParams : ParticipantsParams
        ) : Parcelable

    }