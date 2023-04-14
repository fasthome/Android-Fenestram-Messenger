/**
 * Created by Dmitry Popov on 15.09.2022.
 */
package io.fasthome.fenestram_messenger.settings_impl.data.repo_impl

import io.fasthome.fenestram_messenger.data.UserStorage
import io.fasthome.fenestram_messenger.settings_impl.data.service.SettingsService
import io.fasthome.fenestram_messenger.settings_impl.domain.repo.SettingsRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult

class SettingsRepoImpl(private val settingsService : SettingsService, private val userStorage : UserStorage) : SettingsRepo {
    override suspend fun deleteAccount(): CallResult<Unit> = callForResult {
        settingsService.deleteAccount(userStorage.getUserId() ?: throw Throwable())
    }
}