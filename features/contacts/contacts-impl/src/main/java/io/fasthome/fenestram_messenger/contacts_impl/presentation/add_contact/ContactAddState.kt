package io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact

data class ContactAddState(
    val isNameFilled: Boolean,
    val isNumberEmpty: Boolean,
    val isNumberCorrect: Boolean,
    val isButtonEnabled: Boolean
)