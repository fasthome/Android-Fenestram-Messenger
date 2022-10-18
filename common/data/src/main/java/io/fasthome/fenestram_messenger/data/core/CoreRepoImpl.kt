/**
 * Created by Dmitry Popov on 13.10.2022.
 */
package io.fasthome.fenestram_messenger.data.core

import io.fasthome.fenestram_messenger.data.CoreStorage

class CoreRepoImpl(
    private val coreStorage: CoreStorage
) : CoreRepo {

    override suspend fun getAppOpenCount(): Long = coreStorage.getAppOpenCount()

    override suspend fun plusAppOpenCount() = coreStorage.plusOpenCount()


}