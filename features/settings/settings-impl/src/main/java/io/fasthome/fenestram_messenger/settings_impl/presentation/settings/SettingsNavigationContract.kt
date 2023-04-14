package io.fasthome.fenestram_messenger.settings_impl.presentation.settings

import io.fasthome.fenestram_messenger.navigation.contract.ComponentFragmentContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.settings_api.SettingsInterface

val SettingsComponentContract =
    ComponentFragmentContract<SettingsInterface, NoParams, SettingsFragment>()
