package io.fasthome.fenestram_messenger.auth_impl.domain.logic

import io.fasthome.fenestram_messenger.auth_impl.domain.repo.ProfileRepo
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData
import io.fasthome.fenestram_messenger.util.onSuccess

class ProfileInteractor(
    private val profileRepo: ProfileRepo,
) {

    suspend fun sendPersonalData(personalData: PersonalData) =
        profileRepo.sendPersonalData(personalData).onSuccess { }
}