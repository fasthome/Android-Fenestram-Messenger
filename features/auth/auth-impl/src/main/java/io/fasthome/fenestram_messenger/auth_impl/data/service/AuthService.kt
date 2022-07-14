/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.data.service

import android.util.Log
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.CodeRequest
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.LoginRequest
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.LoginResponse
import io.fasthome.fenestram_messenger.core.exceptions.WrongServerResponseException
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.tokens.AccessToken
import io.fasthome.network.tokens.RefreshToken
import io.fasthome.network.tokens.TokensRepo
import io.ktor.client.features.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AuthService(
    clientFactory: NetworkClientFactory,
    private val tokensRepo: TokensRepo
) {
    private val client = clientFactory.create()

    suspend fun isUserAuthorized(): Boolean = tokensRepo.isHaveRefreshToken()

    suspend fun sendCode(phoneNumber: String) {
        client.runPost<CodeRequest, String>(
            path = "api/v1/authorization/send_code",
            body = CodeRequest(phoneNumber)
        )
    }

    suspend fun login(phoneNumber: String, code: String) {
        try {
            val response = client.runPost<LoginRequest, LoginResponse>(
                path = "api/v1/authorization/login",
                body = LoginRequest(phoneNumber, code)
            )

            tokensRepo.saveTokens(
                accessToken = AccessToken(response.accessToken),
                refreshToken = RefreshToken("refresh")
            )
            Log.d("token", response.accessToken)
        } catch (e : WrongServerResponseException){
            Log.d("token", "null")
        }
    }
}