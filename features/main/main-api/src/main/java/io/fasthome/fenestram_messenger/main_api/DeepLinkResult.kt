package io.fasthome.fenestram_messenger.main_api

import io.fasthome.fenestram_messenger.navigation.contract.CreateParamsInterface
import io.fasthome.fenestram_messenger.navigation.model.IDeepLinkResult
import kotlinx.parcelize.Parcelize

sealed interface DeepLinkResult : IDeepLinkResult {
    @Parcelize
    data class NewChain(val screens: List<CreateParamsInterface>) : DeepLinkResult {

        constructor(vararg screens: CreateParamsInterface) : this(screens.toList())

        init {
            check(screens.isNotEmpty()) {
                "Return null from DeepLinkDelegate.handle, if deepLink can't be handled!"
            }
        }
    }

    @Parcelize
    data class MainScreen(val tab: MainFeature.TabType) : DeepLinkResult
}