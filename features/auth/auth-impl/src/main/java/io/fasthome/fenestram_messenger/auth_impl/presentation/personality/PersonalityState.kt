package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

sealed class PersonalityState {
    object CorrectPersonalityState : PersonalityState()
    object UncorrectPersonalityState : PersonalityState()
    object BeginCodePersonalityState : PersonalityState()
}