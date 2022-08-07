/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.domain.logic

import io.fasthome.fenestram_messenger.auth_impl.data.storage.UserStorage
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.domain.repo.AuthRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.onSuccess
import io.fasthome.network.tokens.TokensRepo

class AuthInteractor(
    private val authRepo: AuthRepo,
    private val tokensRepo: TokensRepo
) {

    suspend fun isUserAuthorized(): CallResult<Boolean> =
        authRepo.isUserAuthorized()

    suspend fun login(phoneNumber: String, code: String) =
        authRepo.login(phoneNumber, code).onSuccess {
            if (it is LoginResult.Success)
                onLoginResultSuccess(tokensRepo = tokensRepo, authRepo = authRepo, loginResult = it)
        }


    suspend fun sendCode(phoneNumber: String) = authRepo.sendCode(phoneNumber).onSuccess { }

    suspend fun getUserId() = authRepo.getUserId()


    companion object {
        suspend fun onLoginResultSuccess(
            tokensRepo: TokensRepo,
            authRepo: AuthRepo,
            loginResult: LoginResult.Success,
        ) {
            tokensRepo.saveTokens(loginResult.accessToken, loginResult.refreshToken)
            authRepo.saveUserId(loginResult.userId.toLong())
        }
    }
}