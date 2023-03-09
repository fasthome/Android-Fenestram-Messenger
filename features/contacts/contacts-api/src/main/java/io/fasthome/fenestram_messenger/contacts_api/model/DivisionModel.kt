package io.fasthome.fenestram_messenger.contacts_api.model

data class DivisionModel(
    val id: Long,
    val title: String,
    val employee: List<Contact>
)