package io.fasthome.fenestram_messenger.profile_impl.domain.logic

import io.fasthome.fenestram_messenger.profile_api.model.PersonalData
import io.fasthome.fenestram_messenger.profile_impl.domain.repo.ProfileRepo
import java.util.*

class ProfileInteractor(
    private val profileRepo: ProfileRepo,
) {

    suspend fun sendPersonalData(personalData: PersonalData) =
        profileRepo.sendPersonalData(personalData)

    suspend fun getPersonalData() =
        profileRepo.getPersonalData()

    /**
     * Метод для установки/обновления аватарки пользователя
     * @param photoBytes аватар
     */
    suspend fun uploadProfileImage(photoBytes: ByteArray) =
        profileRepo.uploadProfileImage(photoBytes, UUID.randomUUID().toString())


}