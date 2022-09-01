/**
 * Created by Dmitry Popov on 30.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_api

import io.fasthome.fenestram_messenger.navigation.contract.ComponentFragmentContractApi
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

interface GroupGuestFeature {

    val groupGuestComponentContract : ComponentFragmentContractApi<GroupParticipantsInterface, ParticipantsParams>

}