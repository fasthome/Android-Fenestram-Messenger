package io.fasthome.fenestram_messenger.profile_guest_impl.data.repo_impl

import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.ProfileGuestService
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.repo.ProfileGuestRepo
import io.fasthome.fenestram_messenger.util.callForResult

class ProfileGuestImpl(private val profileGuestService: ProfileGuestService) : ProfileGuestRepo {
    override suspend fun deleteChat(id: Int) = callForResult {
        profileGuestService.deleteChat(id)
    }
}