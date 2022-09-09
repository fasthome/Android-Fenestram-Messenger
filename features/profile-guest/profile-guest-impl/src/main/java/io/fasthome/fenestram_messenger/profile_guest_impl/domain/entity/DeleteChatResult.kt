package io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity

sealed class DeleteChatResult {
    object Success : DeleteChatResult()
}