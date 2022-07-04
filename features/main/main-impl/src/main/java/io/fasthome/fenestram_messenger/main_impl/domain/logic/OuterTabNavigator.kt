/**
 * Created by Eugeny Kabak on 09.07.2021
 */
package io.fasthome.fenestram_messenger.main_impl.domain.logic

import io.fasthome.fenestram_messenger.main_api.MainFeature
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class OuterTabNavigator {
    private val _tabToOpenFlow = Channel<MainFeature.TabType>(onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val tabToOpenFlow: Flow<MainFeature.TabType> = _tabToOpenFlow.receiveAsFlow()

    fun openTab(tab: MainFeature.TabType) {
        _tabToOpenFlow.trySend(tab)
    }
}