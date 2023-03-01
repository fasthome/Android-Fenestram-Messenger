package io.fasthome.fenestram_messenger.auth_ad_impl.domain.logic

import io.fasthome.fenestram_messenger.auth_ad_impl.domain.repo.AuthAdRepo

class AuthAdInteractor(private val authAdRepo: AuthAdRepo) {
    suspend fun login(login: String, password: String) = authAdRepo.login(login, password)
}