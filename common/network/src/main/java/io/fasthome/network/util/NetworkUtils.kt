package io.fasthome.network.util

import io.fasthome.fenestram_messenger.core.exceptions.WrongServerResponseException
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.tokens.AccessToken
import io.ktor.client.request.*
import io.ktor.http.*

object NetworkUtils {
    private const val BEARER = "Bearer"

    fun buildAuthHeader(accessToken: AccessToken) = "$BEARER ${accessToken.s}"
}

val HttpRequestData.authHeader: String? get() = this.headers[HttpHeaders.Authorization]

fun <T> BaseResponse<T>.requireData(): T = when {
    error != null -> throw WrongServerResponseException()
    else -> data!!
}
