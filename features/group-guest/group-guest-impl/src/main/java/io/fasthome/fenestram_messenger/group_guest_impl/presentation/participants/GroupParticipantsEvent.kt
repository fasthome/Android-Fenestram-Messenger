package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants

import io.fasthome.component.person_detail.PersonDetail

sealed interface GroupParticipantsEvent {
    class MenuOpenEvent(val id: Long, val name: String, val phone: String) : GroupParticipantsEvent
    class ShowPersonDetailDialog(val selectedPerson: PersonDetail) : GroupParticipantsEvent
}