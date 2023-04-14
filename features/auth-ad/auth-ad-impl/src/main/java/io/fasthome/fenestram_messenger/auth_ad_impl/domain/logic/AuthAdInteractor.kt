package io.fasthome.fenestram_messenger.auth_ad_impl.domain.logic

import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.repo.AuthAdRepo
import io.fasthome.network.tokens.TokensRepo

class AuthAdInteractor(
    private val authAdRepo: AuthAdRepo,
    private val tokensRepo: TokensRepo
) {
    suspend fun login(login: String, password: String) = authAdRepo.login(login, password)

    suspend fun onLoginResultSuccess(loginResult: LoginResult.Success) {
        tokensRepo.saveTokens(loginResult.accessToken, loginResult.refreshToken)
        authAdRepo.saveUserId(loginResult.userId)
    }

    suspend fun getUsers() = authAdRepo.getUsers()
    suspend fun logout() = authAdRepo.logout()
}