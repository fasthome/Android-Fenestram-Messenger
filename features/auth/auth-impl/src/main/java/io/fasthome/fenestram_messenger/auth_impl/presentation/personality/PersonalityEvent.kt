package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

sealed class PersonalityEvent {
    object IndefiniteError : PersonalityEvent()
    object LaunchGallery : PersonalityEvent()
}