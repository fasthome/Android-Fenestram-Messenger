package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import io.fasthome.fenestram_messenger.profile_impl.presentation.profile.model.StatusViewItem

sealed interface ProfileEvent {
    class AvatarLoading(val isLoading: Boolean) : ProfileEvent

    class ShowStatusSelectionDialog(val items: List<StatusViewItem>) : ProfileEvent

    class UpdateStatus(val status : StatusViewItem) : ProfileEvent
}