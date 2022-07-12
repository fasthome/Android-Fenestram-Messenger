package io.fasthome.fenestram_messenger.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

object DispatchersProvider {
    val Default: CoroutineDispatcher get() = Dispatchers.Default
    val IO: CoroutineDispatcher get() = Dispatchers.IO
    val Unconfined: CoroutineDispatcher get() = Dispatchers.Unconfined
    val Main: MainCoroutineDispatcher get() = Dispatchers.Main
}