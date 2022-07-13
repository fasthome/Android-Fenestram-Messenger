/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.domain.logic

import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.domain.repo.AuthRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.network.tokens.AccessToken
import io.fasthome.network.tokens.RefreshToken
import io.fasthome.network.tokens.TokensRepo

class AuthInteractor(
    private val authRepo: AuthRepo,
    private val tokensRepo: TokensRepo
) {

    suspend fun isUserAuthorized(): CallResult<Boolean> =
        authRepo.isUserAuthorized()

    suspend fun login(code : String) {
        authRepo.login(code)
        onLoginResultSuccess(
            tokensRepo = tokensRepo,
            loginResult = LoginResult.Success(AccessToken("access"), RefreshToken("refresh"), "userid")
        )
    }

    suspend fun sendCode(phoneNumber : String) {
        authRepo.sendCode(phoneNumber)
    }

    companion object {
        suspend fun onLoginResultSuccess(
            tokensRepo: TokensRepo,
            loginResult: LoginResult.Success,
        ) {
            tokensRepo.saveTokens(loginResult.accessToken, loginResult.refreshToken)
        }
    }
}