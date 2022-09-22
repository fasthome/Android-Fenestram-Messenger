package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.AnotherUserViewItem
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.CurrentUserViewItem
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.kotlin.ifOrNull

fun userToParticipantsItem(user: User, currentId: Long?): ParticipantsViewItem {
    return if (user.id == currentId)
        CurrentUserViewItem(
            userId = user.id,
            name = PrintableText.Raw(
                (if (user.contactName.isNullOrEmpty()) {
                    user.name
                } else {
                    user.contactName
                } ?: (user.name))
                        + (" (Вы)")),
            avatar = user.avatar
        )
    else
        AnotherUserViewItem(
            userId = user.id,
            name = PrintableText.Raw(user.contactName ?: user.name),
            avatar = user.avatar
        )
}

fun participantsViewItemToDifferentUsers(
    user: ParticipantsViewItem,
    currentId: Long?
): ParticipantsViewItem {
    return if (user.userId == currentId)
        CurrentUserViewItem(
            userId = user.userId,
            name = user.name,
            avatar = user.avatar
        )
    else
        AnotherUserViewItem(
            userId = user.userId,
            name = user.name,
            avatar = user.avatar
        )
}