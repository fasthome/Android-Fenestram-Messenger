/**
 * Created by Dmitry Popov on 05.09.2022.
 */
package io.fasthome.fenestram_messenger.push_impl.data.repo_impl

import io.fasthome.fenestram_messenger.push_impl.data.service.PushService
import io.fasthome.fenestram_messenger.push_impl.data.storage.PushesStorage
import io.fasthome.fenestram_messenger.push_impl.domain.entity.DeviceTokenResult
import io.fasthome.fenestram_messenger.push_impl.domain.repo.PushRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult

class PushRepoImpl(private val pushService: PushService, private val pushesStorage: PushesStorage) : PushRepo {

    override suspend fun sendTestPush(): CallResult<Unit> = callForResult {
        pushService.sendTestPush()
    }

    override suspend fun sendPushToken(): CallResult<List<String>> =
        when (val deviceTokenResult = pushesStorage.getDeviceToken()) {
            DeviceTokenResult.PlayServicesNotAvailable -> {
                CallResult.Success(listOf())
            }
            is DeviceTokenResult.TokenReceivingError -> {
                CallResult.Error(deviceTokenResult.error)
            }
            is DeviceTokenResult.TokenReceived -> callForResult {
                pushService.sendPushToken(
                    deviceToken = deviceTokenResult.deviceToken,
                )
            }
        }


    override suspend fun clearPushToken(): CallResult<Unit> = callForResult {
        pushService.clearPushToken()
    }

}