/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.domain.logic

import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.domain.repo.AuthRepo
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.network.tokens.TokensRepo

class AuthInteractor(
    private val authRepo: AuthRepo,
    private val tokensRepo: TokensRepo
) {

    suspend fun isUserAuthorized(): CallResult<Boolean> =
        authRepo.isUserAuthorized()

    suspend fun login(phoneNumber: String, code: String, callback: LoginResult.() -> Unit) {
        authRepo.login(phoneNumber, code) {
            if (this is LoginResult.Success)
                onLoginResultSuccess(tokensRepo = tokensRepo, loginResult = this)
            callback(this)
        }
    }

    suspend fun sendCode(phoneNumber: String, callback: LoginResult.() -> Unit) {
        authRepo.sendCode(phoneNumber) {
            callback(this)
        }
    }

    suspend fun sendPersonalData(personalData: PersonalData, callBack: Boolean.() -> Unit) {
        authRepo.sendPersonalData(personalData) {
            callBack(this)
        }
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