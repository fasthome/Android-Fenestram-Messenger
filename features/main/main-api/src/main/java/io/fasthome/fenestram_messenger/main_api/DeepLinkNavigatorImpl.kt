package io.fasthome.fenestram_messenger.main_api

import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.DeepLinkNavigator
import io.fasthome.fenestram_messenger.navigation.model.IDeepLinkResult

class DeepLinkNavigatorImpl(
    private val router: ContractRouter,
    private val mainFeature: MainFeature
) : DeepLinkNavigator {

    override fun navigateToDeepLink(iDeepLinkResult: IDeepLinkResult) {
        when (val deepLinkResult = iDeepLinkResult as DeepLinkResult) {
            is DeepLinkResult.MainScreen -> mainFeature.returnToMainScreenAndOpenTab(deepLinkResult.tab)
            is DeepLinkResult.NewChain -> router.newChain(*deepLinkResult.screens.toTypedArray())
        }.let { }
    }
}