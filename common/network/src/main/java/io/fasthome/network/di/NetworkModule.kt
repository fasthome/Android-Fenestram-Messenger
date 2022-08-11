package io.fasthome.network.di

import io.fasthome.fenestram_messenger.data.StorageQualifier
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.single
import io.fasthome.network.client.JwtNetworkClientFactory
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.client.SimpleNetworkClientFactory
import io.fasthome.network.tokens.*
import io.fasthome.network.util.NetworkLogger
import io.fasthome.network.util.NetworkController
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.logging.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

object NetworkModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
    )

    private fun createFeatureModule() = module {
        single(
            definition = {
                JwtNetworkClientFactory(
                    httpClientEngine = get(),
                    environment = get(),
                    baseUrl = get<Environment>().endpoints.apiBaseUrl,
                    networkLogger = get(),
                    tokensRepo = get(),
                    forceLogoutManager = lazy { get() },
                )
            },
            qualifier = named(NetworkClientFactoryQualifier.Authorized)
        ) bindSafe NetworkClientFactory::class

        single(
            definition = {
                SimpleNetworkClientFactory(
                    httpClientEngine = get(),
                    environment = get(),
                    baseUrl = get<Environment>().endpoints.refreshTokenUrl,
                    networkLogger = get()
                )
            },
            qualifier = named(NetworkClientFactoryQualifier.RefreshToken)
        ) bindSafe NetworkClientFactory::class

        single(
            definition = {
                SimpleNetworkClientFactory(
                    httpClientEngine = get(),
                    environment = get(),
                    baseUrl = get<Environment>().endpoints.apiBaseUrl,
                    networkLogger = get()
                )
            },
            qualifier = named(NetworkClientFactoryQualifier.Unauthorized)
        ) bindSafe NetworkClientFactory::class

        single {
            OkHttp.create()
        } bindSafe HttpClientEngine::class

        single(::InMemoryTokensStorage)
        single { RefreshTokenStorage(get(named(StorageQualifier.Secure))) }
        single { AccessTokenStorage(get(named(StorageQualifier.Secure))) }

        single(::TokensRepoImpl) bindSafe TokensRepo::class

        single { TokensService(get(named(NetworkClientFactoryQualifier.RefreshToken))) }

        factory(::NetworkController)

        single(::NetworkLogger) bindSafe Logger::class

    }
}