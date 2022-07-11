package io.fasthome.fenestram_messenger.di

import io.fasthome.fenestram_messenger.BuildConfig
import io.fasthome.fenestram_messenger.core.environment.Endpoints
import io.fasthome.fenestram_messenger.core.environment.Environment
import org.koin.dsl.module

object EnvironmentModule {
    operator fun invoke() = module {
        single {

            val apiBaseUrl = BuildConfig.MAIN_API_BASE_URL_DEV
            val refreshTokenUrl = BuildConfig.REFRESH_TOKEN_URL_DEV

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