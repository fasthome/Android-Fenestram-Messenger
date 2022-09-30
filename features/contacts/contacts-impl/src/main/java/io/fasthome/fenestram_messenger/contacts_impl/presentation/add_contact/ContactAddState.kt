package io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact

sealed class ContactAddState {
    data class ContactAddStatus(
        val contactAddStatus: ContactAddFragment.EditTextStatus
    ) : ContactAddState()

    data class ContactAutoFillStatus(val name: String?, val phone: String?): ContactAddState()
}
