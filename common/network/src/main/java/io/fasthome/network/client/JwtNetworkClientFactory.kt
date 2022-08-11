package io.fasthome.network.client

import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.core.exceptions.UnauthorizedException
import io.fasthome.network.tokens.TokenUpdateException
import io.fasthome.network.tokens.TokensRepo
import io.fasthome.network.util.NetworkUtils
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class JwtNetworkClientFactory(
    private val httpClientEngine: HttpClientEngine,
    private val environment: Environment,
    private val baseUrl: String,
    private val networkLogger: Logger,
    private val tokensRepo: TokensRepo,
    //Циклическая зависимость. LogoutManager зависит внутри от ForceLogoutManager через
    //JwtNetworkClientFactory, тк по факту они не зависят, решается lazy инициализацией
    private val forceLogoutManager: Lazy<ForceLogoutManager>,
) : NetworkClientFactory {

    override fun create(
        adjustClientBlock: HttpClientConfig<*>.() -> Unit,
    ): NetworkClient = SimpleNetworkClientFactory(
        httpClientEngine = httpClientEngine,
        environment = environment,
        baseUrl = baseUrl,
        networkLogger = networkLogger
    ).create {

        defaultRequestSuspend {
            val accessToken = tokensRepo.getAccessToken()
            headers.remove(HttpHeaders.Authorization)
            header(HttpHeaders.Authorization, NetworkUtils.buildAuthHeader(accessToken))
        }

        install(NeedRetry) {
            retryCondition { _: HttpRequest, response: HttpResponse ->
                if (response.status.value == HttpStatusCode.Unauthorized.value) {
//                    tokensRepo.updateToken()
                    forceLogoutManager.value.forceLogout()
                    true
                } else {
                    false
                }
            }
        }

        HttpResponseValidator {
            handleResponseException { cause: Throwable ->
                when (cause) {
                    is TokenUpdateException, is UnauthorizedException -> forceLogoutManager.value.forceLogout()
                }
            }
        }

        adjustClientBlock()
    }
}