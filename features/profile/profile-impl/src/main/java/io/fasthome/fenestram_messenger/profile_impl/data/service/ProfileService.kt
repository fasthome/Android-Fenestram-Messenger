package io.fasthome.fenestram_messenger.profile_impl.data.service

import io.fasthome.fenestram_messenger.profile_impl.data.service.model.ProfileImageResponse
import io.fasthome.fenestram_messenger.profile_impl.data.service.model.ProfileRequest
import io.fasthome.fenestram_messenger.profile_api.model.PersonalData
import io.fasthome.fenestram_messenger.profile_api.model.ProfileImageResult
import io.fasthome.fenestram_messenger.profile_impl.data.service.mapper.ProfileMapper
import io.fasthome.fenestram_messenger.profile_impl.data.service.model.ProfileResponse
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.util.requireData

class ProfileService(
    clientFactory: NetworkClientFactory,
    private val profileMapper: ProfileMapper
) {
    private val client = clientFactory.create()

    suspend fun sendPersonalData(personalData: PersonalData): PersonalData {
        val body = with(personalData) {
            ProfileRequest(username, nickname, email, birth, avatar, playerId)
        }

        return client
            .runPatch<ProfileRequest, BaseResponse<ProfileResponse>>(
                path = "api/v1/profile",
                body = body
            )
            .requireData()
            .let(profileMapper::responseToPersonalData)
    }

    suspend fun uploadProfileImage(photoBytes: ByteArray, guid: String): ProfileImageResult {
        val response = client
            .runSubmitFormWithFile<BaseResponse<ProfileImageResponse>>(
                path = "api/v1/files/upload",
                binaryData = photoBytes,
                filename = "$guid.jpg",
            )
            .requireData()
        return ProfileImageResult(profileImagePath = response.pathToFile)
    }

    suspend fun getProfile(): PersonalData =
        client
            .runGet<BaseResponse<ProfileResponse>>(path = "api/v1/profile")
            .requireData()
            .let(profileMapper::responseToPersonalData)
}
