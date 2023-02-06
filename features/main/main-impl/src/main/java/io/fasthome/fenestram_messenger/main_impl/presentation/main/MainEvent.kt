/**
 * Created by Dmitry Popov on 22.05.2022.
 */
package io.fasthome.fenestram_messenger.main_impl.presentation.main

sealed interface MainEvent {
    class UpdateBadge(val count : Int) : MainEvent
}