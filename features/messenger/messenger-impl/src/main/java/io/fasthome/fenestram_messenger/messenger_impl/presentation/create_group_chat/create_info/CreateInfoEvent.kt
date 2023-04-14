package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info

sealed interface CreateInfoEvent {
    object OpenCamera : CreateInfoEvent
    object OpenImagePicker : CreateInfoEvent
}