package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

sealed class WelcomeEvent {
    object ConnectionError : WelcomeEvent()
    object IndefiniteError : WelcomeEvent()
}