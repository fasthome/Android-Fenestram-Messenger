/**
 * Created by Dmitry Popov on 01.07.2022.
 */
package io.fasthome.fenestram_messenger.main_impl

import io.fasthome.fenestram_messenger.main_api.MainFeature
import io.fasthome.fenestram_messenger.main_impl.presentation.main.MainNavigationContract
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

class MainFeatureImpl : MainFeature {
    override val mainNavigationContract = MainNavigationContract
}