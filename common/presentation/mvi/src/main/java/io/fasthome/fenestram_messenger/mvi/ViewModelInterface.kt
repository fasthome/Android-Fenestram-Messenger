package io.fasthome.fenestram_messenger.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ViewModelInterface<out S : Any, out E : Any> {

    val viewState: StateFlow<S>

    val viewEvent: Flow<E>

    fun onBackPressed(): Boolean

    fun onMessageResult(result: MessageResult)

    fun onViewActive()
    fun onViewInactive()
    fun onCreate()
}