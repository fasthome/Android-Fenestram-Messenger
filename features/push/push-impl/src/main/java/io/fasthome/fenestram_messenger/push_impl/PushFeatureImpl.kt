/**
 * Created by Dmitry Popov on 05.09.2022.
 */
package io.fasthome.fenestram_messenger.push_impl

import io.fasthome.fenestram_messenger.push_api.PushFeature
import io.fasthome.fenestram_messenger.push_impl.domain.repo.PushRepo
import io.fasthome.fenestram_messenger.util.CallResult

class PushFeatureImpl(private val pushRepo: PushRepo) : PushFeature {

    override suspend fun sendTestPush(): CallResult<Unit> = pushRepo.sendTestPush()

    override suspend fun updateToken(): CallResult<Unit> = pushRepo.sendPushToken()

}