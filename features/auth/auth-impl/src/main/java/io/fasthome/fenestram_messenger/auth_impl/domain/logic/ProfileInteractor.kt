package io.fasthome.fenestram_messenger.auth_impl.domain.logic

import io.fasthome.fenestram_messenger.auth_impl.domain.repo.ProfileRepo
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData

class ProfileInteractor(
    private val profileRepo: ProfileRepo,
) {

    suspend fun sendPersonalData(personalData: PersonalData, callBack: Boolean.() -> Unit) {
        profileRepo.sendPersonalData(personalData) {
            callBack(this)
        }
    }

}