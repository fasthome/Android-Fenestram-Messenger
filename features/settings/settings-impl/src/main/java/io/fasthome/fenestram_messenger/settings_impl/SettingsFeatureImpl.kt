/**
 * Created by Dmitry Popov on 20.07.2022.
 */
package io.fasthome.fenestram_messenger.settings_impl


import io.fasthome.fenestram_messenger.navigation.contract.ComponentFragmentContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.settings_api.SettingsFeature
import io.fasthome.fenestram_messenger.settings_api.SettingsInterface
import io.fasthome.fenestram_messenger.settings_impl.presentation.settings.SettingsComponentContract

class SettingsFeatureImpl : SettingsFeature {

    override val settingsComponentContract: ComponentFragmentContractApi<SettingsInterface, NoParams> = SettingsComponentContract
}