/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.data.service

import android.util.Log
import io.fasthome.fenestram_messenger.auth_impl.data.service.mapper.AuthMapper
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.CodeRequest
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.LoginRequest
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.LoginResponse
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData
import io.fasthome.fenestram_messenger.core.exceptions.InternetConnectionException
import io.fasthome.fenestram_messenger.core.exceptions.WrongServerResponseException
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.tokens.AccessToken
import io.fasthome.network.tokens.RefreshToken
import io.fasthome.network.tokens.TokensRepo

class AuthService(
    clientFactory: NetworkClientFactory,
    private val tokensRepo: TokensRepo
) {
    private val client = clientFactory.create()

    suspend fun isUserAuthorized(): Boolean = tokensRepo.isHaveRefreshToken()

    suspend fun sendCode(phoneNumber: String, callback: LoginResult.() -> Unit) {
        try {
            client.runPost<CodeRequest, String>(
                path = "api/v1/authorization/send_code",
                body = CodeRequest(phoneNumber)
            )
            callback(LoginResult.SuccessSendRequest)
        } catch (e: Exception) {
            callback(LoginResult.ConnectionError)
        }

    }

    suspend fun login(phoneNumber: String, code: String) : LoginResult {
        val response : BaseResponse<LoginResponse> = client.runPost(
            path = "api/v1/authorization/login",
            body = LoginRequest(phoneNumber, code)
        )

        return AuthMapper.responseToLogInResult(response)
    }
}