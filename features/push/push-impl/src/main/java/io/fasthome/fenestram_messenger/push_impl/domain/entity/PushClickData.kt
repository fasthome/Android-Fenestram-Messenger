package io.fasthome.fenestram_messenger.push_impl.domain.entity

sealed interface PushClickData {

    data class SettlementEvent(val draftGuid: String) : PushClickData

    object Unknown : PushClickData
}