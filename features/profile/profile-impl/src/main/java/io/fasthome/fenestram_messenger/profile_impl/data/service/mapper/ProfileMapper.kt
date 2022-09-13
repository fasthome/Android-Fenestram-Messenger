package io.fasthome.fenestram_messenger.profile_impl.data.service.mapper

import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.profile_api.entity.PersonalData
import io.fasthome.fenestram_messenger.profile_impl.data.service.model.ProfileResponse

class ProfileMapper {

    fun responseToPersonalData(response: ProfileResponse): PersonalData =
            PersonalData(
                username = response.name,
                nickname = response.nickname,
                birth = response.birth,
                email = response.email,
                avatar = response.avatar,
                playerId = ""
            )
}