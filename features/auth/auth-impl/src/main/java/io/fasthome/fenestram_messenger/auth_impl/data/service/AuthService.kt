/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.data.service

import com.bumptech.glide.load.model.Headers
import io.fasthome.fenestram_messenger.auth_impl.data.service.mapper.CodeMapper
import io.fasthome.fenestram_messenger.auth_impl.data.service.mapper.LoginMapper
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.CodeRequest
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.LoginRequest
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.LoginResponse
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.CodeResult
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseMessageResponse
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.tokens.TokensRepo
import java.util.Collections.singletonList
import java.util.Collections.singletonMap

class AuthService(
    clientFactory: NetworkClientFactory,
    private val tokensRepo: TokensRepo,
    private val loginMapper: LoginMapper,
    private val environment: Environment
) {
    private val client = clientFactory.create()

    suspend fun isUserAuthorized(): Boolean = tokensRepo.isHaveRefreshToken()

    suspend fun sendCode(phoneNumber: String): CodeResult {
        val response: BaseResponse<String> = client.runPost(
            path = "authorization/send_code",
            body = CodeRequest(phoneNumber)
        )

        return CodeMapper.responseToLogInResult(response)
    }

    suspend fun login(phoneNumber: String, code: String): LoginResult {
        val response: BaseResponse<LoginResponse> = client.runPost(
            path = "authorization/login",
            body = LoginRequest(phoneNumber, code),
        )

        return loginMapper.responseToLogInResult(response)
    }

    suspend fun logoutAccount(): BaseMessageResponse? {
        val response: BaseResponse<BaseMessageResponse> = client.runPost(
            path = "authorization/logout",
            body = Unit,
            customHeaders = mapOf("Authorization" to "Bearer ${tokensRepo.getAccessToken().s}")
        )

        return response.data
    }
}