/**
 * Created by Vladimir Rudakov on 21.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_guest_api

import android.os.Parcelable
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import kotlinx.parcelize.Parcelize

interface ProfileGuestFeature {
    val profileGuestNavigationContract: NavigationContractApi<ProfileGuestParams, ProfileGuestResult>

    @Parcelize
    class ProfileGuestParams(
        val id: Long?,
        val userName: String,
        val userNickname: String,
        val userPhone: String,
        val userAvatar: String,
        val chatParticipants: List<User>,
        val isGroup: Boolean,
        val editMode: Boolean
    ) : Parcelable

    sealed class ProfileGuestResult : Parcelable {

        @Parcelize
        class ChatDeleted(val id: Long) : ProfileGuestResult()

        @Parcelize
        object Canceled : ProfileGuestResult()
    }
}