package io.fasthome.fenestram_messenger.util.view_action

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class BottomViewAction<T> {

    private val actionsChannel = Channel<T>(onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val actionsToBottomView = Channel<T>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun sendActionToFragment(data: T) {
        actionsChannel.trySend(data)
    }

    fun sendActionToBottomView(data: T) {
        actionsToBottomView.trySend(data)
    }

    fun receiveActionsToBottomView() = actionsToBottomView.receiveAsFlow()

    fun receiveActionsToFragment() = actionsChannel.receiveAsFlow()

}