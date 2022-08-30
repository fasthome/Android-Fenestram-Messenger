/**
 * Created by Dmitry Popov on 30.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl

import io.fasthome.fenestram_messenger.group_guest_api.GroupGuestFeature
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.GroupGuestContract
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

class GroupGuestFeatureImpl : GroupGuestFeature {

    override val groupGuestNavigationContract: NavigationContractApi<NoParams, NoResult> = GroupGuestContract

}