package io.fasthome.fenestram_messenger.auth_impl.data.repo_impl

import io.fasthome.fenestram_messenger.auth_impl.data.service.ProfileService
import io.fasthome.fenestram_messenger.auth_impl.domain.repo.ProfileRepo
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData
import io.fasthome.fenestram_messenger.util.callForResult

class ProfileRepoImpl(
    private val profileService: ProfileService
) : ProfileRepo {
    override suspend fun sendPersonalData(
        personalData: PersonalData,
    ) = callForResult { profileService.sendPersonalData(personalData) }
}
