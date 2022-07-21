/**
 * Created by Dmitry Popov on 19.07.2022.
 */
package io.fasthome.fenestram_messenger.debug_api

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

interface DebugFeature {

    val navigationContract: NavigationContractApi<NoParams, NoResult>

}