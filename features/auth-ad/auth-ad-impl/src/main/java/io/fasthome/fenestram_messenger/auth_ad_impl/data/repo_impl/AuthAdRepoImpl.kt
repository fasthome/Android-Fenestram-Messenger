package io.fasthome.fenestram_messenger.auth_ad_impl.data.repo_impl

import io.fasthome.fenestram_messenger.auth_ad_impl.data.service.AuthAdService
import io.fasthome.fenestram_messenger.auth_ad_impl.data.service.UsersService
import io.fasthome.fenestram_messenger.auth_ad_impl.data.storage.MockLoginStorage
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.UsersResult
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.repo.AuthAdRepo
import io.fasthome.fenestram_messenger.data.UserStorage
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult

class AuthAdRepoImpl(
    private val authService: AuthAdService,
    private val usersService: UsersService,
    private val userStorage: UserStorage,
    private val mockLoginStorage: MockLoginStorage
) :
    AuthAdRepo {
    override suspend fun login(username: String, password: String) = callForResult {
        authService.login(username, password)
//        mockLoginStorage.login(username, password)
    }

    override suspend fun saveUserId(userId: Long) {
        userStorage.setUserId(userId)
    }

    override suspend fun getUsers(): CallResult<UsersResult> = callForResult {
        usersService.getUsers()
    }

    override suspend fun logout() {
        usersService.logoutUser()
    }
}