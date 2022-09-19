package io.fasthome.fenestram_messenger.settings_impl.presentation.settings

interface SettingsEvent {

    object DeleteAccount : SettingsEvent
    object Logout : SettingsEvent

}