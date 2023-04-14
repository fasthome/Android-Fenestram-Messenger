/**
 * Created by Dmitry Popov on 10.01.2023.
 */
package io.fasthome.fenestram_messenger.call_impl

import io.fasthome.fenestram_messenger.call_api.CallFeature
import io.fasthome.fenestram_messenger.call_impl.core.sipdroid.sipua.ui.Sipdroid
import io.fasthome.fenestram_messenger.call_impl.presentation.create_call.CreateCallNavigationContract

class CallFeatureImpl : CallFeature {

    override val navigationContract = CreateCallNavigationContract

    override val demoIntentActivityClass: Class<*> = Sipdroid::class.java

}