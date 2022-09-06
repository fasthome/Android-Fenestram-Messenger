/**
 * Created by Dmitry Popov on 30.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl

import io.fasthome.fenestram_messenger.group_guest_api.GroupGuestFeature
import io.fasthome.fenestram_messenger.group_guest_api.GroupParticipantsInterface
import io.fasthome.fenestram_messenger.group_guest_api.ParticipantsParams
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.GroupParticipantsComponentContract
import io.fasthome.fenestram_messenger.navigation.contract.ComponentFragmentContractApi

class GroupGuestFeatureImpl : GroupGuestFeature {

    override val groupGuestComponentContract: ComponentFragmentContractApi<GroupParticipantsInterface, ParticipantsParams> = GroupParticipantsComponentContract

}