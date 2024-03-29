/**
 * Created by Dmitry Popov on 18.07.2022.
 */
package io.fasthome.fenestram_messenger.debug_impl.presentation.debug

import io.fasthome.component.person_detail.PersonDetail
import io.fasthome.fenestram_messenger.core.debug.EndpointsConfig

sealed interface DebugEvent {

    object ContactsDeleted : DebugEvent
    object RebirthApplication : DebugEvent
    class AcceptEnvChangeDialog(val endpointsConfig: EndpointsConfig) : DebugEvent
    class ShowPersonDetailDialog(val selectedPerson: PersonDetail) : DebugEvent
    class CopyTokenEvent(val token: String) : DebugEvent

}