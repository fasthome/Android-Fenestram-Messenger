/**
 * Created by Vladimir Rudakov on 21.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_guest_api

import android.os.Parcelable
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import kotlinx.parcelize.Parcelize

interface ProfileGuestFeature {
    val profileGuestNavigationContract: NavigationContractApi<ProfileGuestParams, NoResult>

    @Parcelize
    class ProfileGuestParams(
        val userName : String,
        val userNickname : String,
        val userAvatar : String,
        val chatParticipants : List<User>,
        val isGroup : Boolean
    ) : Parcelable
}