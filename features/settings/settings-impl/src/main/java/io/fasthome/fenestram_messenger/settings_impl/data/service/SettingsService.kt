/**
 * Created by Dmitry Popov on 15.09.2022.
 */
package io.fasthome.fenestram_messenger.settings_impl.data.service

import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.util.requireData

class SettingsService (clientFactory: NetworkClientFactory) {

    private val client = clientFactory.create()

    suspend fun deleteAccount(userId : Long) =
        client
            .runDelete<BaseResponse<Unit>>(
                path = "api/v1/users/$userId"
            )
            .requireData()

}