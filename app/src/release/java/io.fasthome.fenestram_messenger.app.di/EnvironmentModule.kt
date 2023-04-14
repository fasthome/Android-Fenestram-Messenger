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


            val apiBaseUrl = BuildConfig.MAIN_API_BASE_URL_DEV + "api/${BuildConfig.DEV_API_VERSION}/"

            val policyUrl = BuildConfig.POLICY_RULES_URL

            Environment(
                endpoints = Endpoints(
                    baseUrl = BuildConfig.MAIN_API_BASE_URL_DEV,
                    apiBaseUrl = apiBaseUrl,
                    policyUrl = policyUrl
                ),
                isDebug = BuildConfig.IS_DEBUG,
                apiVersion = BuildConfig.DEV_API_VERSION,
                appVersion = BuildConfig.VERSION_NAME
            )
        }
    }
}