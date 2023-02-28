package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import androidx.annotation.StringRes

sealed interface MessengerToolbarMode {
    object Default: MessengerToolbarMode
    data class Select(@StringRes val titleRes: Int): MessengerToolbarMode
    object Search: MessengerToolbarMode
}