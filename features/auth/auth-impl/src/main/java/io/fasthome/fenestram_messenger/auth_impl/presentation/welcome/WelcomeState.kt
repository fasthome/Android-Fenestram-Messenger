package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

sealed class WelcomeState {
    object CorrectPhoneNumberState : WelcomeState()
    object UncorrectPhoneNumberState : WelcomeState()
    object BeginWelcomeState : WelcomeState()
}