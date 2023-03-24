/**
 * Created by Vladimir Rudakov on 24.03.2023.
 */
package io.fasthome.fenestram_messenger.tasks_api

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.UnitResult

interface TasksFeature {
    val tasksNavigationContract: NavigationContractApi<NoParams, UnitResult>
    val taskEditorNavigationContract: NavigationContractApi<NoParams, UnitResult>
}