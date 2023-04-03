/**
 * Created by Vladimir Rudakov on 24.03.2023.
 */
package io.fasthome.fenestram_messenger.tasks_impl

import io.fasthome.fenestram_messenger.tasks_api.TasksFeature
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.TaskEditorNavigationContract
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.TasksNavigationContract

class TasksFeatureImpl : TasksFeature {
    override val tasksNavigationContract = TasksNavigationContract
    override val taskEditorNavigationContract = TaskEditorNavigationContract
}