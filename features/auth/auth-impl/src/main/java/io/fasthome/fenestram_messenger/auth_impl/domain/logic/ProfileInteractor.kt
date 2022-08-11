package io.fasthome.fenestram_messenger.auth_impl.domain.logic

import io.fasthome.fenestram_messenger.auth_impl.domain.repo.ProfileRepo
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData
import io.fasthome.fenestram_messenger.util.onSuccess
import java.util.*

class ProfileInteractor(
    private val profileRepo: ProfileRepo,
) {

    suspend fun sendPersonalData(personalData: PersonalData) =
        profileRepo.sendPersonalData(personalData).onSuccess { }

    /**
     * Метод для установки/обновления аватарки пользователя
     * @param photoBytes аватар
     */
    suspend fun uploadProfileImage(photoBytes: ByteArray) =
        profileRepo.uploadProfileImage(photoBytes, UUID.randomUUID().toString())
}