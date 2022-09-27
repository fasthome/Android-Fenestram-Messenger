package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants

import android.view.View

sealed interface GroupParticipantsEvent {
    class MenuOpenEvent(val id: Long, val name: String, val phone: String) : GroupParticipantsEvent
}