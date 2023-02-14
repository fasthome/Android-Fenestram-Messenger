package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

sealed interface ProfileEvent {
    class AvatarLoading(val isLoading: Boolean): ProfileEvent
}