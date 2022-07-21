/**
 * Created by Dmitry Popov on 22.05.2022.
 */
package io.fasthome.fenestram_messenger.main_impl.presentation.main

import io.fasthome.fenestram_messenger.main_api.MainFeature

data class MainState(
    val currentTab: MainFeature.TabType,
    val debugVisible : Boolean
)