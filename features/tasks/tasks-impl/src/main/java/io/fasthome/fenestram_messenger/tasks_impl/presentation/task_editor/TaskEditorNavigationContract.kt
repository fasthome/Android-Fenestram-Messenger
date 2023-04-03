package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.UnitResult

object TaskEditorNavigationContract : NavigationContract<NoParams, UnitResult>(TaskEditorFragment::class)