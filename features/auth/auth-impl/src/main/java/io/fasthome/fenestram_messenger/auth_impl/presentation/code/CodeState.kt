package io.fasthome.fenestram_messenger.auth_impl.presentation.code

sealed class CodeState {
    object CorrectCodeState : CodeState()
    object UncorrectCodeState : CodeState()
    object BeginCodeState : CodeState()
}