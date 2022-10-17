/**
 * Created by Dmitry Popov on 13.10.2022.
 */
package io.fasthome.fenestram_messenger.data

import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import kotlinx.coroutines.withContext

class CoreStorage(
    persistentStorageFactory: KeyValueStorage.Factory,
) {

    private val preferencesStorage = persistentStorageFactory.create("core.prefs")

    private var appOpenCount: Long? by preferencesStorage.stored("KEY_APP_OPEN_COUNT")

    suspend fun getAppOpenCount(): Long =
        withContext(DispatchersProvider.IO) {
            return@withContext appOpenCount ?: 0
        }

    suspend fun plusOpenCount() =
        withContext(DispatchersProvider.IO) {
            this@CoreStorage.appOpenCount = getAppOpenCount() + 1
        }

}