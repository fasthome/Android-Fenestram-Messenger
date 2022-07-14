/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.data.service

import android.util.Log
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.CodeRequest
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.LoginRequest
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.LoginResponse
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData
import io.fasthome.fenestram_messenger.core.exceptions.InternetConnectionException
import io.fasthome.fenestram_messenger.core.exceptions.WrongServerResponseException
import io.fasthome.network.client.NetworkClientFactory
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

    suspend fun login(phoneNumber: String, code: String, callback: suspend LoginResult.() -> Unit) {
        try {
            val response = client.runPost<LoginRequest, LoginResponse>(
                path = "api/v1/authorization/login",
                body = LoginRequest(phoneNumber, code)
            )

            tokensRepo.saveTokens(
                accessToken = AccessToken(response.accessToken),
                refreshToken = RefreshToken("refresh")
            )

            callback(
                LoginResult.Success(
                    accessToken = AccessToken(response.accessToken),
                    RefreshToken("refresh"),
                    response.id.toString()
                )
            )

        } catch (e: InternetConnectionException) {
            callback(LoginResult.ConnectionError)
        } catch (e: WrongServerResponseException) {
            callback(LoginResult.WrongCode)
        }
    }

    suspend fun sendPersonalData(personalData: PersonalData, callback: Boolean.() -> Unit) {
        // TODO Доделать с заголовком
        try {
            val params = with(personalData) {
                mapOf(
                    "name" to name,
                    "nickname" to userName,
                    "email" to email,
                    "birth" to birth,
                    "avatar" to avatar,
                    "player_id" to player_id
                )
            }
            Log.d("params", params.toString())
            client.runPatch<String>(
                path = "api/v1/profile",
                params = params
            )
        } catch (e: Exception) {
            callback(false)
        }
    }
}