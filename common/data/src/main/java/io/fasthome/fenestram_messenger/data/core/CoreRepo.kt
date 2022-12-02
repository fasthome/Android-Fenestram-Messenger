/**
 * Created by Dmitry Popov on 13.10.2022.
 */
package io.fasthome.fenestram_messenger.data.core

interface CoreRepo {

    suspend fun getAppOpenCount(): Long
    suspend fun plusAppOpenCount()
    suspend fun clearStorage()
}