/**
 * Created by Dmitry Popov on 02.02.2023.
 */
package io.fasthome.fenestram_messenger.messenger_impl.domain.logic

import io.fasthome.fenestram_messenger.messenger_api.entity.Badge
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

/**
 * Прослойка в виде отдельной сущности необходима из за того, что экземпляры [MessengerInteractor] из разных мест инжекта будут разные,
 * но [BadgeCounter] создается как single сущность, поэтому даже из разных мест обращения всегда будет единый экземпляр [BadgeCounter]
 */
class BadgeCounter {

    private val _unreadCountChannel =
        Channel<Badge>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val unreadCountFlow: Flow<Badge> = _unreadCountChannel.receiveAsFlow()

    fun subscribe(): Flow<Badge> = unreadCountFlow

    fun sendCount(badge : Badge) {
        _unreadCountChannel.trySend(badge)
    }

    suspend fun getBadge() = unreadCountFlow.last()

}