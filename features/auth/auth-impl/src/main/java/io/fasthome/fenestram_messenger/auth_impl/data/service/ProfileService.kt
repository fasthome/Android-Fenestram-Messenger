package io.fasthome.fenestram_messenger.auth_impl.data.service

import io.fasthome.fenestram_messenger.auth_impl.data.service.mapper.ProfileMapper
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.ProfileRequest
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.ProfileResponse
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.ProfileResult
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse

class ProfileService(
    clientFactory: NetworkClientFactory,
) {
    private val client = clientFactory.create()

    suspend fun sendPersonalData(personalData: PersonalData): ProfileResult {
        val body = with(personalData) {
            ProfileRequest(name, userName, email, birth/*, avatar, player_id*/)
        }

        val response: BaseResponse<ProfileResponse> = client.runPatch(
            path = "api/v1/profile",
            body = body
        )

        return ProfileMapper.responseToLogInResult(response)
    }
}
