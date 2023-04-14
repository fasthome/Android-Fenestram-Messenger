package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

sealed class PersonalityEvent {
    object LaunchGallery : PersonalityEvent()

    class Loading(val isLoading : Boolean) : PersonalityEvent()

    object ShowSelectFromDialog : PersonalityEvent()

}