package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants

import android.view.View

sealed interface GroupParticipantsEvent {
    class MenuOpenEvent(val id: Long, val view: View) : GroupParticipantsEvent
}