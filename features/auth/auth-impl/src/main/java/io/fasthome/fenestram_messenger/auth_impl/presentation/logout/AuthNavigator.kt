package io.fasthome.fenestram_messenger.auth_impl.presentation.logout

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class AuthNavigator {

    private val startAuthChannel = Channel<AuthFeature.AuthEvent>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun startAuth() {
        val authEvent = AuthFeature.AuthEvent.LaunchWelcome
        startAuthChannel.trySend(authEvent)
    }

    val startAuthEvents: Flow<AuthFeature.AuthEvent> = startAuthChannel.receiveAsFlow()
}