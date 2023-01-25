/**
 * Created by Dmitry Popov on 20.07.2022.
 */
package io.fasthome.fenestram_messenger.settings_api

import io.fasthome.fenestram_messenger.navigation.contract.ComponentFragmentContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams

interface SettingsFeature {

    val settingsComponentContract: ComponentFragmentContractApi<SettingsInterface, NoParams>

}