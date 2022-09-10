/**
 * Created by Vladimir Rudakov on 21.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_guest_impl

import io.fasthome.fenestram_messenger.group_guest_api.ParticipantsParams
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.contract.mapParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.ProfileGuestNavigationContract

class ProfileGuestFeatureImpl : ProfileGuestFeature {
    override val profileGuestNavigationContract: NavigationContractApi<ProfileGuestFeature.ProfileGuestParams, NoResult> =
        ProfileGuestNavigationContract.mapParams {
            ProfileGuestNavigationContract.Params(
                id = it.id,
                userNickname = it.userNickname,
                userName = it.userName,
                userAvatar = it.userAvatar,
                groupParticipantsParams = ParticipantsParams(it.chatParticipants),
                isGroup = it.isGroup
            )
        }
}