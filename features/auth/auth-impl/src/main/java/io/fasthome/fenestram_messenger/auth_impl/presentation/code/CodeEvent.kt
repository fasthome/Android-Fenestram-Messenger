package io.fasthome.fenestram_messenger.auth_impl.presentation.code

sealed class CodeEvent {
    object ConnectionError : CodeEvent()
    object IndefiniteError : CodeEvent()
}