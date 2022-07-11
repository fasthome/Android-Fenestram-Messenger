package io.fasthome.network.client

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*

/**
 * Need retry condition checker
 *
 * You could return true to make [HttpClient] retry the request
 */
typealias RetryCondition = suspend (request: HttpRequest, response: HttpResponse) -> Boolean

/**
 * Need retry feature for [HttpClient]
 *
 * @property retryHandlers: list of retry conditions to check before deciding to retry a request
 */
class NeedRetry(
    private val retryHandlers: List<RetryCondition>,
) {

    /**
     * [NeedRetry] configuration.
     */
    class Config {
        internal val retryHandlers: MutableList<RetryCondition> = mutableListOf()

        /**
         * Add [retryCondition].
         * Last added handler executes first.
         */
        fun retryCondition(block: RetryCondition) {
            retryHandlers += block
        }
    }

    companion object : HttpClientFeature<Config, NeedRetry> {
        override val key: AttributeKey<NeedRetry> = AttributeKey("NeedRetry")

        override fun prepare(block: Config.() -> Unit): NeedRetry {
            val config = Config().apply(block)
            return NeedRetry(config.retryHandlers)
        }

        override fun install(feature: NeedRetry, scope: HttpClient) {
            scope.receivePipeline.intercept(HttpReceivePipeline.After) {
                val requestBuilder = HttpRequestBuilder().takeFrom(context.request)
                val isRetryNeeded =
                    feature.retryHandlers.any { it(context.request, context.response) }

                if (isRetryNeeded) {
                    proceedWith(context.client!!.request(requestBuilder))
                } else {
                    proceedWith(it)
                }
            }
        }
    }
}