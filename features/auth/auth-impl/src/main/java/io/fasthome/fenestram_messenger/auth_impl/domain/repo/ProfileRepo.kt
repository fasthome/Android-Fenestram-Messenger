package io.fasthome.fenestram_messenger.auth_impl.domain.repo

import io.fasthome.fenestram_messenger.auth_impl.domain.entity.ProfileResult
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData
import io.fasthome.fenestram_messenger.util.CallResult

interface ProfileRepo {
    suspend fun sendPersonalData(personalData: PersonalData) : CallResult<ProfileResult>
}