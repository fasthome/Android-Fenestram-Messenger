package io.fasthome.fenestram_messenger.profile_impl.domain.repo

import io.fasthome.fenestram_messenger.profile_api.model.PersonalData
import io.fasthome.fenestram_messenger.profile_api.model.ProfileImageResult
import io.fasthome.fenestram_messenger.util.CallResult

interface ProfileRepo {
    suspend fun sendPersonalData(personalData: PersonalData): CallResult<Unit>
    suspend fun uploadProfileImage(photoBytes: ByteArray, guid: String): CallResult<ProfileImageResult>
    suspend fun getPersonalData(): CallResult<PersonalData>
}