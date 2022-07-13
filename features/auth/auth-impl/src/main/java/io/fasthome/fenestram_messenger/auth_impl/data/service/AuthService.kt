/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.data.service

import io.fasthome.fenestram_messenger.auth_impl.data.service.model.CodeRequest
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.tokens.TokensRepo

class AuthService(
    clientFactory: NetworkClientFactory,
    private val tokensRepo: TokensRepo
) {
    private val client = clientFactory.create()

    suspend fun isUserAuthorized(): Boolean = tokensRepo.isHaveRefreshToken()

    suspend fun sendCode(phoneNumber : String) {
        client.runPost<CodeRequest,String>(
            path = "api/v1/authorization/send_code",
            body = CodeRequest(phoneNumber)
        )
    }
}