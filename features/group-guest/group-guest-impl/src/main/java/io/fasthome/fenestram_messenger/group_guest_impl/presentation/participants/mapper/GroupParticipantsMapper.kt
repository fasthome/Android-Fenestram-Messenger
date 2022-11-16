package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.AnotherUserViewItem
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.CurrentUserViewItem
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.messenger_api.entity.ChatUsersResult
import io.fasthome.fenestram_messenger.util.Country
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setMaskByCountry

fun userToParticipantsItem(user: User, currentId: Long?): ParticipantsViewItem {
    return if (user.id == currentId)
        CurrentUserViewItem(
            userId = user.id,
            name = PrintableText.Raw(
                user.name + " (Вы)"
            ),
            avatar = user.avatar
        )
    else
        AnotherUserViewItem(
            userId = user.id,
            name = PrintableText.Raw(getName(user)),
            avatar = user.avatar
        )
}

fun chatUserToParticipantsViewItem(
    selfUserId: Long?,
    chatUserResult: ChatUsersResult,
): ParticipantsViewItem {
    return if (chatUserResult.userId == selfUserId)
        CurrentUserViewItem(
            userId = chatUserResult.userId,
            name = PrintableText.Raw("${chatUserResult.userName} (Вы)"),
            avatar = chatUserResult.avatar
        )
    else
        AnotherUserViewItem(
            userId = chatUserResult.userId,
            name = PrintableText.Raw(chatUserResult.userName),
            avatar = chatUserResult.avatar
        )
}

private fun getName(user: User): String {
    return when {
        user.contactName?.isNotEmpty() == true -> user.contactName!!
        user.name.isNotEmpty() -> user.name
        else -> user.phone.setMaskByCountry(Country.RUSSIA)
    }
}