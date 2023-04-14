/**
 * Created by Dmitry Popov on 18.07.2022.
 */
package io.fasthome.fenestram_messenger.debug_impl.presentation.debug

import io.fasthome.fenestram_messenger.core.debug.EndpointsConfig

data class DebugState(
    val userId: String,
    val token: String,
    val featuresVisible: Boolean,
    val componentsVisible: Boolean,
    val requestsVisible: Boolean,
    val loginVisible: Boolean,
    val userCode: String,
    val userPhone: String,
    val selectedEnv: EndpointsConfig
)