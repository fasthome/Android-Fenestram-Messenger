/**
 * Created by Dmitry Popov on 10.01.2023.
 */
package io.fasthome.fenestram_messenger.call_impl.presentation.call

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

object CallNavigationContract : NavigationContract<NoParams, NoResult>(CallFragment::class)