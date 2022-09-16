/**
 * Created by Dmitry Popov on 15.09.2022.
 */
package io.fasthome.fenestram_messenger.settings_impl.domain.repo

import io.fasthome.fenestram_messenger.util.CallResult

interface SettingsRepo {

    suspend fun deleteAccount() : CallResult<Unit>

}