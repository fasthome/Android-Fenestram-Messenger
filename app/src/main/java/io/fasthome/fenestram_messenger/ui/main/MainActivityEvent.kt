/**
 * Created by Dmitry Popov on 13.10.2022.
 */
package io.fasthome.fenestram_messenger.ui.main

sealed interface MainActivityEvent {

    object StartSplashEvent : MainActivityEvent

}