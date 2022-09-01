package io.fasthome.fenestram_messenger.profile_impl.data.repo_impl

import io.fasthome.fenestram_messenger.profile_impl.data.service.ProfileService
import io.fasthome.fenestram_messenger.profile_api.entity.PersonalData
import io.fasthome.fenestram_messenger.profile_impl.domain.repo.ProfileRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult

class ProfileRepoImpl(
    private val profileService: ProfileService
) : ProfileRepo {
    override suspend fun sendPersonalData(
        personalData: PersonalData,
    ) = callForResult { profileService.sendPersonalData(personalData) }

    override suspend fun uploadProfileImage(photoBytes: ByteArray, guid: String) =
        callForResult { profileService.uploadProfileImage(photoBytes, guid) }

    override suspend fun getPersonalData(): CallResult<PersonalData> =
        callForResult {
            profileService.getProfile()
        }


}
