/**
 * Created by Dmitry Popov on 20.07.2022.
 */
package io.fasthome.fenestram_messenger.settings_impl


import io.fasthome.fenestram_messenger.settings_api.SettingsFeature
import io.fasthome.fenestram_messenger.settings_impl.presentation.settings.SettingsNavigationContract

class SettingsFeatureImpl : SettingsFeature {
    override val settingsNavigationContract = SettingsNavigationContract
}