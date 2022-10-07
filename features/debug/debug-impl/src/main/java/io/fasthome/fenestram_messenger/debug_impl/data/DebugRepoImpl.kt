package io.fasthome.fenestram_messenger.debug_impl.data

import io.fasthome.fenestram_messenger.core.debug.DebugRepo
import io.fasthome.fenestram_messenger.core.debug.EndpointsConfig
import io.fasthome.fenestram_messenger.data.KeyValueStorage
import io.fasthome.fenestram_messenger.data.stored


class DebugRepoImpl(
    storageFactory: KeyValueStorage.Factory,
) : DebugRepo {
    private val storage = storageFactory.create("debug")

    override var endpointsConfig: EndpointsConfig by storage.stored("endpointsConfig", EndpointsConfig.Dev)
}