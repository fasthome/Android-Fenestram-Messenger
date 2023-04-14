/**
 * Created by Vladimir Rudakov on 24.03.2023.
 */
package io.fasthome.fenestram_messenger.tasks_impl

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.contract.map
import io.fasthome.fenestram_messenger.tasks_api.TasksFeature
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.TaskEditorNavigationContract
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.TasksNavigationContract

class TasksFeatureImpl : TasksFeature {
    override val tasksNavigationContract = TasksNavigationContract
    override val taskEditorNavigationContract: NavigationContractApi<TasksFeature.TasksParams, TasksFeature.TasksResult> =
        TaskEditorNavigationContract.map(
            paramsMapper = {
                TaskEditorNavigationContract.Params(
                    task = it.task,
                    mode = it.editorMode,
                )
            },
            resultMapper = {
                when (it) {
                    is TaskEditorNavigationContract.Result.TaskCreated -> TasksFeature.TasksResult.TaskCreated
                    is TaskEditorNavigationContract.Result.TaskDeleted -> TasksFeature.TasksResult.TaskDeleted
                    is TaskEditorNavigationContract.Result.TaskEdited -> TasksFeature.TasksResult.TaskEdited
                    is TaskEditorNavigationContract.Result.Canceled -> TasksFeature.TasksResult.Canceled
                }
            })
}