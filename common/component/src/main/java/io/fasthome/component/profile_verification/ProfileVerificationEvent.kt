package io.fasthome.component.profile_verification

sealed interface ProfileVerificationEvent {
    object OpenCamera : ProfileVerificationEvent
    object OpenImagePicker : ProfileVerificationEvent
}