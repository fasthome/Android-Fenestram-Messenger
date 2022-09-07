/**
 * Created by Dmitry Popov on 18.07.2022.
 */
package io.fasthome.fenestram_messenger.debug_impl.presentation.debug

sealed interface DebugEvent {

    object ContactsDeleted : DebugEvent

}