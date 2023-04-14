package io.fasthome.fenestram_messenger.messenger_api.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageInfo(
    val id: Long,
    val type: MessageType
) : Parcelable