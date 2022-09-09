package io.fasthome.fenestram_messenger.profile_guest_impl.domain.logic

import io.fasthome.fenestram_messenger.profile_guest_impl.domain.repo.ProfileGuestRepo
import io.fasthome.fenestram_messenger.util.onSuccess

class DeleteChatUseCase(private val profileGuestRepo: ProfileGuestRepo) {

    suspend fun deleteChat(id: Int) = profileGuestRepo.deleteChat(id).onSuccess { }
}