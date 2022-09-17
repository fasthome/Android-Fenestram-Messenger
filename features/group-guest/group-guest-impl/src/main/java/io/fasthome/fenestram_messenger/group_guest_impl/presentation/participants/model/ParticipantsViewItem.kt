package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model

import android.os.Parcelable
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParticipantsViewItem(
    val userId : Long,
    val name : PrintableText,
    val avatar : String
) : Parcelable
