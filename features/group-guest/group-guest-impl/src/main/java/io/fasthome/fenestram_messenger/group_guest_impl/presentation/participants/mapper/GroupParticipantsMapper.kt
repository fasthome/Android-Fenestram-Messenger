package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.AnotherUserViewItem
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.CurrentUserViewItem
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.util.Country
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setMaskByCountry

fun userToParticipantsItem(user: User, selfUserId: Long?): ParticipantsViewItem {
    return if (user.id == selfUserId)
        CurrentUserViewItem(
            userId = user.id,
            name = PrintableText.Raw(
                getName(user) + " (Вы)"
            ),
            avatar = user.avatar,
            nickname = user.nickname,
            phone = user.phone
        )
    else
        AnotherUserViewItem(
            userId = user.id,
            name = PrintableText.Raw(getName(user)),
            avatar = user.avatar,
            nickname = user.nickname,
            phone = user.phone
        )
}

private fun getName(user: User): String {
    return when {
        user.contactName?.isNotEmpty() == true -> user.contactName!!
        user.name.isNotEmpty() -> user.name
        else -> user.phone.setMaskByCountry(Country.RUSSIA)
    }
}