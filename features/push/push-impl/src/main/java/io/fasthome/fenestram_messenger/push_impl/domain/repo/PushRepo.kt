/**
 * Created by Dmitry Popov on 05.09.2022.
 */
package io.fasthome.fenestram_messenger.push_impl.domain.repo

import io.fasthome.fenestram_messenger.util.CallResult

interface PushRepo {

    suspend fun sendTestPush() : CallResult<Unit>

    suspend fun sendPushToken() : CallResult<Unit>

    suspend fun clearPushToken() : CallResult<Unit>

}