/**
 * Created by Dmitry Popov on 19.07.2022.
 */
package io.fasthome.fenestram_messenger.debug_impl

import io.fasthome.fenestram_messenger.debug_api.DebugFeature
import io.fasthome.fenestram_messenger.debug_impl.presentation.debug.DebugNavigationContract

class DebugFeatureImpl : DebugFeature {

    override val navigationContract = DebugNavigationContract

}