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

            val apiVersion = when (config) {
                EndpointsConfig.Dev -> BuildConfig.DEV_API_VERSION
                EndpointsConfig.Prod -> BuildConfig.PROD_API_VERSION
            }

            val baseUrl = when (config) {
                EndpointsConfig.Dev -> BuildConfig.MAIN_API_BASE_URL_DEV
                EndpointsConfig.Prod -> BuildConfig.MAIN_API_BASE_URL_PROD
            }

            val apiBaseUrl = when (config) {
                EndpointsConfig.Dev -> BuildConfig.MAIN_API_BASE_URL_DEV + "api/${BuildConfig.DEV_API_VERSION}/"
                EndpointsConfig.Prod -> BuildConfig.MAIN_API_BASE_URL_PROD + "api/${BuildConfig.PROD_API_VERSION}/"
            }

            val refreshTokenUrl = when (config) {
                EndpointsConfig.Dev -> BuildConfig.REFRESH_TOKEN_URL_DEV + "api/${BuildConfig.DEV_API_VERSION}/"
                EndpointsConfig.Prod -> BuildConfig.REFRESH_TOKEN_URL_PROD + "api/${BuildConfig.PROD_API_VERSION}/"
            }

            Environment(
                endpoints = Endpoints(
                    baseUrl = baseUrl,
                    apiBaseUrl = apiBaseUrl,
                    refreshTokenUrl = refreshTokenUrl,
                ),
                isDebug = BuildConfig.IS_DEBUG,
                apiVersion = apiVersion
            )
        }
    }
}