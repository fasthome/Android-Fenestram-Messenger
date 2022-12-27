package io.fasthome.fenestram_messenger.app.di

import io.fasthome.fenestram_messenger.core.debug.DebugRepo
import io.fasthome.fenestram_messenger.core.debug.EndpointsConfig
import org.koin.dsl.module
import io.fasthome.fenestram_messenger.BuildConfig
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.core.environment.Endpoints

object EnvironmentModule {
    operator fun invoke() = module {
        single {
            val repo = get<DebugRepo>()


            val apiBaseUrl = BuildConfig.MAIN_API_BASE_URL_PROD + "api/${BuildConfig.PROD_API_VERSION}/"

            val refreshTokenUrl = BuildConfig.REFRESH_TOKEN_URL_PROD + "api/${BuildConfig.PROD_API_VERSION}/"

            Environment(
                endpoints = Endpoints(
                    baseUrl = BuildConfig.MAIN_API_BASE_URL_PROD,
                    apiBaseUrl = apiBaseUrl,
                    refreshTokenUrl = refreshTokenUrl,
                ),
                isDebug = BuildConfig.IS_DEBUG,
                apiVersion = BuildConfig.PROD_API_VERSION
            )
        }
    }
}