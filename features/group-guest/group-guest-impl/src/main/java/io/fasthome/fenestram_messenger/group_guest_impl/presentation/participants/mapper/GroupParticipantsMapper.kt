package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.util.PrintableText

fun userToParticipantsItem(user : User) = ParticipantsViewItem(
    userId = user.id,
    name = PrintableText.Raw(user.name),
    avatar = user.avatar
)