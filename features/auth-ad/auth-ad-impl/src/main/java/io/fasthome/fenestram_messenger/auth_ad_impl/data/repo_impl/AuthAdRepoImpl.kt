package io.fasthome.fenestram_messenger.auth_ad_impl.data.repo_impl

import io.fasthome.fenestram_messenger.auth_ad_impl.data.service.AuthAdService
import io.fasthome.fenestram_messenger.auth_ad_impl.data.storage.MockLoginStorage
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.repo.AuthAdRepo
import io.fasthome.fenestram_messenger.util.callForResult

class AuthAdRepoImpl(private val authService: AuthAdService, private val mockLoginStorage: MockLoginStorage) :
    AuthAdRepo {
    override suspend fun login(login: String, password: String) = callForResult {
//        authService.login(login, password)
        mockLoginStorage.login(login, password)
    }
}