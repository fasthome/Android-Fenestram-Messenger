package io.fasthome.fenestram_messenger.navigation

import androidx.annotation.DrawableRes

interface FabConsumer {
    /**
     * @return true, if consumed.
     */
    fun onFabClicked(): Boolean

    fun updateFabIcon(@DrawableRes iconRes : Int?, badgeCount : Int)
}