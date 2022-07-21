/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl

import io.fasthome.fenestram_messenger.profile_api.ProfileFeature
import io.fasthome.fenestram_messenger.profile_impl.presentation.profile.ProfileNavigationContract

class ProfileFeatureImpl : ProfileFeature {
    override val profileNavigationContract = ProfileNavigationContract
}