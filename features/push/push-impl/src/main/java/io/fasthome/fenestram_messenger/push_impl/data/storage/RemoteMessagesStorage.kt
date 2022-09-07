package io.fasthome.fenestram_messenger.push_impl.data.storage

import io.fasthome.fenestram_messenger.data.KeyValueStorage
import io.fasthome.fenestram_messenger.data.stored


class RemoteMessagesStorage(
    storageFactory: KeyValueStorage.Factory,
) {
    private val storage = storageFactory.create("remote_messages")

    private var nextNotificationId: Int by storage.stored("nextNotificationId", 1)

    @Synchronized
    fun nextNotificationId(): Int = nextNotificationId++
}