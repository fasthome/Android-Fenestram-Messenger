package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model

import android.os.Parcelable
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.parcelize.Parcelize

@Parcelize
open class ParticipantsViewItem(
    open val userId: Long,
    open val name: PrintableText,
    open val nickname: String,
    open val phone: String,
    open val avatar: String
) : Parcelable

@Parcelize
class AnotherUserViewItem(
    override val userId: Long,
    override val name: PrintableText,
    override val nickname: String,
    override val phone: String,
    override val avatar: String
) : ParticipantsViewItem(
    userId, name, nickname, phone, avatar
), Parcelable

@Parcelize
class CurrentUserViewItem(
    override val userId: Long,
    override val name: PrintableText,
    override val nickname: String,
    override val phone: String,
    override val avatar: String
) : ParticipantsViewItem(
    userId, name, nickname, phone, avatar
), Parcelable

