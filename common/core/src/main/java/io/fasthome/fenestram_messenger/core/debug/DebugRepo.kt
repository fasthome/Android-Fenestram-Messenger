package io.fasthome.fenestram_messenger.core.debug

interface DebugRepo {
    var endpointsConfig: EndpointsConfig
}

/**
 * Используется в релизной сборке.
 */
class NormalDebugRepo : DebugRepo {
    override var endpointsConfig: EndpointsConfig
        get() = EndpointsConfig.Prod
        set(_) = Unit
}