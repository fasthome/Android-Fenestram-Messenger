/**
 * Created by Dmitry Popov on 05.09.2022.
 */
package io.fasthome.fenestram_messenger.push_impl.data.service

import io.fasthome.fenestram_messenger.push_impl.data.service.model.SendTokenRequest
import io.fasthome.fenestram_messenger.push_impl.data.service.model.TestPushRequest
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.util.requireData

class PushService(clientFactory: NetworkClientFactory) {

    private val client = clientFactory.create()

    suspend fun sendTestPush() =
        client
            .runPost<TestPushRequest, BaseResponse<Unit>>(
                path = "api/v1/chats/test_push",
                body = TestPushRequest("test", "text")
            )
            .requireData()

    suspend fun sendPushToken(deviceToken: String): List<String> =
        client
            .runPost<SendTokenRequest, BaseResponse<List<String>>>(
                path = "api/v1/authorization/firebase_token",
                body = SendTokenRequest(deviceToken)
            )
            .requireData()

    suspend fun clearPushToken() {

    }
}