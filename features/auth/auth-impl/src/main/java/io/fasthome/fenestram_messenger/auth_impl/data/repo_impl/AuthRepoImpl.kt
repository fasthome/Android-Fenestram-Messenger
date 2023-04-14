/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.data.repo_impl

import io.fasthome.fenestram_messenger.auth_impl.data.service.AuthService
import io.fasthome.fenestram_messenger.auth_impl.data.service.UsersService
import io.fasthome.fenestram_messenger.data.UserStorage
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.UsersResult
import io.fasthome.fenestram_messenger.auth_impl.domain.repo.AuthRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult

class AuthRepoImpl(
    private val authService: AuthService,
    private val userStorage : UserStorage,
    private val usersService: UsersService
) : AuthRepo {

    override suspend fun isUserAuthorized(): CallResult<Boolean> = callForResult {
        authService.isUserAuthorized()
    }

    override suspend fun getUserId(): CallResult<Long?> = callForResult {
        userStorage.getUserId()
    }

    override suspend fun sendCode(phoneNumber: String) = callForResult {
        authService.sendCode(phoneNumber)
    }

    override suspend fun logout() {
        authService.logoutAccount()
    }

    override suspend fun login(
        phoneNumber: String,
        code: String
    ) = callForResult {
        authService.login(phoneNumber, code)
    }

    override suspend fun saveUserId(userId: Long) {
        userStorage.setUserId(userId)
    }

    override suspend fun getUsers(): CallResult<UsersResult> = callForResult {
        usersService.getUsers()
    }

    override suspend fun getUserCode(): CallResult<String?> = callForResult{
        userStorage.getUserCode()
    }

    override suspend fun getUserPhone(): CallResult<String?> = callForResult{
        userStorage.getUserPhone()
    }

    override suspend fun setUserCode(code : String) {
        userStorage.setUserCode(code)
    }

    override suspend fun setUserPhone(phone : String) {
        userStorage.setUserPhone(phone)
    }
}