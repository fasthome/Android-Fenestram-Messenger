/**
 * Created by Dmitry Popov on 01.07.2022.
 */
package io.fasthome.fenestram_messenger.main_impl

import io.fasthome.fenestram_messenger.main_api.MainFeature
import io.fasthome.fenestram_messenger.main_impl.domain.logic.OuterTabNavigator
import io.fasthome.fenestram_messenger.main_impl.presentation.main.MainNavigationContract
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

class MainFeatureImpl(
    private val router: ContractRouter,
    private val outerTabNavigator: OuterTabNavigator,
) : MainFeature {
    override val mainNavigationContract = MainNavigationContract

    override fun returnToMainScreenAndOpenTab(tab: MainFeature.TabType) {
        router.backTo(null)
        outerTabNavigator.openTab(tab)
    }
}