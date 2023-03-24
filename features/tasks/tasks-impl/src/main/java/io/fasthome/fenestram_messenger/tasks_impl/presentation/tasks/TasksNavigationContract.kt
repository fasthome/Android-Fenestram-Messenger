package io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.UnitResult

object TasksNavigationContract : NavigationContract<NoParams, UnitResult>(TasksFragment::class)