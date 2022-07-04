package io.fasthome.fenestram_messenger.navigation

/**
 * [OnBackPressedDispatcher] and [OnBackPressedCallback] do NOT allow to return "consumed" as result.
 */
interface BackPressConsumer {
    /**
     * @return true, if consumed.
     */
    fun onBackPressed(): Boolean
}