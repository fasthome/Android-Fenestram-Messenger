package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model

import io.fasthome.fenestram_messenger.util.PrintableText

data class ParticipantsViewItem(
    val userId : Long,
    val name : PrintableText,
    val avatar : String
)
