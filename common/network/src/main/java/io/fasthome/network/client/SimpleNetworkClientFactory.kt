package io.fasthome.network.client

import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.core.exceptions.InternetConnectionException
import io.fasthome.fenestram_messenger.core.exceptions.WrongServerResponseException
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.time.Duration

internal class SimpleNetworkClientFactory(
    private val httpClientEngine: HttpClientEngine,
    private val environment: Environment,
    private val baseUrl: String,
    private val networkLogger: Logger,
    private val forceLogoutManager: Lazy<ForceLogoutManager>,
) : NetworkClientFactory {

    override fun create(
        adjustClientBlock: HttpClientConfig<*>.() -> Unit,
    ): NetworkClient {
        val httpClient = HttpClient(httpClientEngine) {
            baseConfig()
            adjustClientBlock()
        }

        return NetworkClient(httpClient, baseUrl)
    }

    private fun HttpClientConfig<*>.baseConfig() {
        if (environment.isDebug) {
            install(Logging) {
                level = LogLevel.ALL
                logger = networkLogger
            }
        }

        developmentMode = environment.isDebug

        install(HttpTimeout) {
            connectTimeoutMillis = TIMEOUT.toMillis()
            requestTimeoutMillis = TIMEOUT.toMillis()
            socketTimeoutMillis = TIMEOUT.toMillis()
        }

        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                prettyPrint = environment.isDebug
                ignoreUnknownKeys = true
            })
        }

        HttpResponseValidator {
            handleResponseException { cause: Throwable ->
                when (cause) {
                    is ClientRequestException -> {
                        if (cause.response.status.value == HttpStatusCode.Unauthorized.value ||
                            cause.response.status.value == HttpStatusCode.Forbidden.value
                        ) {
                            forceLogoutManager.value.forceLogout()
                        }
                    }
                    is IOException, is HttpRequestTimeoutException -> throw InternetConnectionException(cause)
                    is ResponseException, is SerializationException -> throw WrongServerResponseException(cause)
                }
            }
        }

        install(CheckBaseResponse)
    }

    companion object {
        val TIMEOUT: Duration = Duration.ofSeconds(30)
    }
}