/**
 * Created by Vladimir Rudakov on 21.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_guest_impl

import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.ProfileGuestNavigationContract

class ProfileGuestFeatureImpl : ProfileGuestFeature {
    override val profileGuestNavigationContract = ProfileGuestNavigationContract
}