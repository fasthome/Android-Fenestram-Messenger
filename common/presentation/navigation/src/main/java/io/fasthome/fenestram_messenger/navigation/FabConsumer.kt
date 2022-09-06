package io.fasthome.fenestram_messenger.navigation

interface FabConsumer {
    /**
     * @return true, if consumed.
     */
    fun onFabClicked(): Boolean
}