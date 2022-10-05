package io.fasthome.fenestram_messenger.app.di

import io.fasthome.fenestram_messenger.BuildConfig
import io.fasthome.fenestram_messenger.core.debug.DebugRepo
import io.fasthome.fenestram_messenger.core.debug.EndpointsConfig
import io.fasthome.fenestram_messenger.core.environment.Endpoints
import io.fasthome.fenestram_messenger.core.environment.Environment
import org.koin.dsl.module

object EnvironmentModule {
    operator fun invoke() = module {
        single {
            val repo = get<DebugRepo>()

            val config = repo.endpointsConfig

            val apiBaseUrl = when (config) {
                EndpointsConfig.Dev -> BuildConfig.MAIN_API_BASE_URL_DEV
                EndpointsConfig.Prod -> BuildConfig.MAIN_API_BASE_URL_PROD
            }

            val refreshTokenUrl = when (config) {
                EndpointsConfig.Dev -> BuildConfig.REFRESH_TOKEN_URL_DEV
                EndpointsConfig.Prod -> BuildConfig.REFRESH_TOKEN_URL_PROD
            }

            Environment(
                endpoints = Endpoints(
                    apiBaseUrl = apiBaseUrl,
                    refreshTokenUrl = refreshTokenUrl,
                ),
                isDebug = BuildConfig.IS_DEBUG,
            )
        }
    }
}