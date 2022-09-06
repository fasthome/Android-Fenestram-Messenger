package io.fasthome.fenestram_messenger.group_guest_api

import android.os.Parcelable
import io.fasthome.fenestram_messenger.contacts_api.model.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParticipantsParams(
    val participants : List<User>
) : Parcelable