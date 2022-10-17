package io.fasthome.network.client

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class NetworkClient(
    @PublishedApi
    internal val httpClient: HttpClient,

    @PublishedApi
    internal val baseUrl: String,
) {
    suspend inline fun <reified Request, reified Response> runPost(
        path: String,
        body: Request,
        useBaseUrl: Boolean = true,
        params: Map<String, Any?> = emptyMap(),
        customHeaders: Map<String, String> = emptyMap(),
        contentType: ContentType = ContentType.Application.Json,
    ): Response = httpClient.post {
        url(buildUrl(path, useBaseUrl))
        contentType(contentType)
        if (body != null) this.body = body
        params.forEach { (t, u) -> parameter(t, u) }
        customHeaders.forEach(headers::append)
    }

    suspend inline fun <reified Response> runGet(
        path: String,
        useBaseUrl: Boolean = true,
        params: Map<String, Any?> = emptyMap(),
        customHeaders: Map<String, String> = emptyMap(),
        contentType: ContentType = ContentType.Application.Json,
    ): Response = httpClient.get {
        url(buildUrl(path, useBaseUrl))
        contentType(contentType)
        params.forEach { (t, u) -> parameter(t, u) }
        customHeaders.forEach(headers::append)
    }

    suspend inline fun <reified Response> runGet(
        path: String,
        useBaseUrl: Boolean = true,
        params: Map<String, Any?> = emptyMap(),
        customHeaders: Map<String, String> = emptyMap(),
        contentType: ContentType = ContentType.Application.Json,
        crossinline progressListener : ProgressListener
    ): Response = httpClient.get {
        url(buildUrl(path, useBaseUrl))
        contentType(contentType)
        params.forEach { (t, u) -> parameter(t, u) }
        customHeaders.forEach(headers::append)
        onDownload { bytesSentTotal, contentLength ->
            val step = contentLength / 100
            val currentProgress = bytesSentTotal / step
            progressListener(currentProgress.toInt())
        }
    }

    suspend inline fun <reified Request, reified Response> runPatch(
        path: String,
        useBaseUrl: Boolean = true,
        body: Request,
        params: Map<String, Any?> = emptyMap(),
        contentType: ContentType = ContentType.Application.Json,
    ): Response = httpClient.patch {
        url(buildUrl(path, useBaseUrl))
        contentType(contentType)
        if (body != null) this.body = body
        params.forEach { (t, u) -> parameter(t, u) }
    }

    suspend inline fun <reified Response> runSubmitForm(
        path: String,
        params: Map<String, String>,
        encodeInQuery: Boolean,
        useBaseUrl: Boolean = true,
    ): Response {
        val formParameters = Parameters.build {
            params.forEach { (key, value) ->
                append(key, value)
            }
        }

        return httpClient.submitForm(
            url = buildUrl(path, useBaseUrl),
            formParameters = formParameters,
            encodeInQuery = encodeInQuery
        )
    }

    suspend inline fun <reified Response> runSubmitFormWithFile(
        path: String,
        binaryData: ByteArray,
        filename: String,
        params: Map<String, Any?> = emptyMap(),
        useBaseUrl: Boolean = true,
    ): Response {
        return httpClient.submitFormWithBinaryData(
            url = buildUrl(path, useBaseUrl),
            formData = formData {
                append("file", binaryData, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=$filename")
                })
            }
        ) {
            params.forEach { (t, u) -> parameter(t, u) }
        }
    }

    suspend inline fun <reified Response> runDelete(
        path: String,
        useBaseUrl: Boolean = true,
        params: Map<String, Any?> = emptyMap()
    ): Response = httpClient.delete {
        url(buildUrl(path, useBaseUrl))
        contentType(ContentType.Application.Json)
        params.forEach { (t, u) -> parameter(t, u) }
    }

    suspend inline fun <reified Request, reified Response> runDelete(
        path: String,
        useBaseUrl: Boolean = true,
        params: Map<String, Any?> = emptyMap(),
        body: Request,
    ): Response = httpClient.delete {
        url(buildUrl(path, useBaseUrl))
        contentType(ContentType.Application.Json)
        params.forEach { (t, u) -> parameter(t, u) }
        if (body != null) this.body = body
    }

    suspend inline fun <reified Request, reified Response> runPut(
        path: String,
        body: Request,
        useBaseUrl: Boolean = true,
        params: Map<String, Any?> = emptyMap(),
    ): Response = httpClient.put {
        url(buildUrl(path, useBaseUrl))
        contentType(ContentType.Application.Json)
        if (body != null) this.body = body
        params.forEach { (t, u) -> parameter(t, u) }
    }


    fun recreate(adjustClientBlock: HttpClientConfig<*>.() -> Unit = {}): NetworkClient {
        val newClient = httpClient.config {
            adjustClientBlock()
        }

        return NetworkClient(newClient, baseUrl)
    }

    @PublishedApi
    internal fun buildUrl(path: String, useBaseUrl: Boolean): String = if (useBaseUrl) {
        baseUrl + path
    } else {
        path
    }
}

typealias ProgressListener = suspend (progress : Int) -> Unit
