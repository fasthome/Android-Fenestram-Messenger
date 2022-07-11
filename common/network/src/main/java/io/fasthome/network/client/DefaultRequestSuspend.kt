package io.fasthome.network.client

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.util.*

typealias DefaultRequestSuspendAction = suspend HttpRequestBuilder.() -> Unit

/**
 * То же самое, что [DefaultRequest], только с поддержкой suspend блока.
 */
class DefaultRequestSuspend(
    private val action: DefaultRequestSuspendAction,
) {

    class Config {
        internal lateinit var action: DefaultRequestSuspendAction

        fun action(block: DefaultRequestSuspendAction) {
            action = block
        }
    }

    companion object : HttpClientFeature<Config, DefaultRequestSuspend> {
        override val key: AttributeKey<DefaultRequestSuspend> = AttributeKey("SuspendDefaultRequest")

        override fun prepare(block: Config.() -> Unit): DefaultRequestSuspend {
            val config = Config().apply(block)
            return DefaultRequestSuspend(config.action)
        }

        override fun install(feature: DefaultRequestSuspend, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Before) {
                feature.action(context)
            }
        }
    }
}

fun HttpClientConfig<*>.defaultRequestSuspend(block: suspend HttpRequestBuilder.() -> Unit) {
    install(DefaultRequestSuspend) {
        action {
            block()
        }
    }
}
