package io.fasthome.network.client

import io.ktor.client.*

interface NetworkClientFactory {
    fun create(
        adjustClientBlock: HttpClientConfig<*>.() -> Unit = {},
    ): NetworkClient
}