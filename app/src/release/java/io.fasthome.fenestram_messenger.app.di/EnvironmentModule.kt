package io.fasthome.fenestram_messenger.app.di

import io.fasthome.fenestram_messenger.core.debug.DebugRepo
import io.fasthome.fenestram_messenger.core.debug.EndpointsConfig
import org.koin.dsl.module

object EnvironmentModule {
    operator fun invoke() = module {
        single {
            val repo = get<DebugRepo>()


            val apiBaseUrl = BuildConfig.MAIN_API_BASE_URL_PROD

            val refreshTokenUrl = BuildConfig.REFRESH_TOKEN_URL_PROD

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