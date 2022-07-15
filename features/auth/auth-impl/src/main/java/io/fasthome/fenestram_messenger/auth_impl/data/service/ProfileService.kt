package io.fasthome.fenestram_messenger.auth_impl.data.service

import io.fasthome.fenestram_messenger.auth_impl.data.service.model.ProfileRequest
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData
import io.fasthome.network.client.NetworkClientFactory

class ProfileService(
    clientFactory: NetworkClientFactory,
) {
    private val client = clientFactory.create()

    suspend fun sendPersonalData(personalData: PersonalData, callback: Boolean.() -> Unit) {
        try {
            val body = with(personalData) {
                ProfileRequest(name, userName, email, birth, avatar, player_id)
            }
            client.runPatch<ProfileRequest, String>(
                path = "api/v1/profile",
                body = body
            )
            callback(true)
        } catch (e: Exception) {
            callback(false)
        }
    }
}
