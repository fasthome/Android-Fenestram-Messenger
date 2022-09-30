package io.fasthome.fenestram_messenger.profile_guest_impl.data.repo_impl

import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.ProfileGuestService
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.UploadImageResult
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.repo.ProfileGuestRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult

class ProfileGuestRepoImpl(private val profileGuestService: ProfileGuestService) :
    ProfileGuestRepo {

    override suspend fun patchChatAvatar(id: Long, avatar: String): CallResult<Unit> =
        callForResult {
            profileGuestService.patchChatAvatar(id, avatar)
        }

    override suspend fun patchChatName(id: Long, name: String): CallResult<Unit> =
        callForResult {
            profileGuestService.patchChatName(id, name)
        }

    override suspend fun uploadImage(
        photoBytes: ByteArray,
        guid: String
    ): CallResult<UploadImageResult> = callForResult {
        profileGuestService.uploadImage(photoBytes, guid)
    }
}