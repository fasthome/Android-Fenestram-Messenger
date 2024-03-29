/**
 * Created by Dmitry Popov on 05.09.2022.
 */
package io.fasthome.fenestram_messenger.push_impl

import android.content.Intent
import io.fasthome.fenestram_messenger.navigation.model.IDeepLinkResult
import io.fasthome.fenestram_messenger.push_api.PushFeature
import io.fasthome.fenestram_messenger.push_impl.domain.repo.PushRepo
import io.fasthome.fenestram_messenger.push_impl.presentation.PushClickHandler
import io.fasthome.fenestram_messenger.util.CallResult

class PushFeatureImpl(
    private val pushRepo: PushRepo,
    private val pushClickHandler: PushClickHandler,
) : PushFeature {

    override suspend fun sendTestPush(): CallResult<Unit> = pushRepo.sendTestPush()

    override suspend fun updateToken(): CallResult<List<String>> = pushRepo.sendPushToken()

    override suspend fun getPushToken(): String = pushRepo.getPushToken()

    override fun handlePushClick(intent: Intent): IDeepLinkResult? = pushClickHandler.handle(intent)

}