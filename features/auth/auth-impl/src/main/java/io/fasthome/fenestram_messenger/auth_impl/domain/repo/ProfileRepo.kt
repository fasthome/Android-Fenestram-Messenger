package io.fasthome.fenestram_messenger.auth_impl.domain.repo

import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData

interface ProfileRepo {
    suspend fun sendPersonalData(personalData: PersonalData, callback: Boolean.() -> Unit)
}