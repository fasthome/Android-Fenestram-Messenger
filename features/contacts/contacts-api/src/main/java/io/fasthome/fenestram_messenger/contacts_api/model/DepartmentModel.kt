package io.fasthome.fenestram_messenger.contacts_api.model

data class DepartmentModel(
    val id: Long,
    val title: String,
    val division: List<DivisionModel>
)