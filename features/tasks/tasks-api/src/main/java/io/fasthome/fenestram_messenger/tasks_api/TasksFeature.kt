/**
 * Created by Vladimir Rudakov on 24.03.2023.
 */
package io.fasthome.fenestram_messenger.tasks_api

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.UnitResult
import io.fasthome.fenestram_messenger.tasks_api.model.EditorMode
import io.fasthome.fenestram_messenger.tasks_api.model.Task
import kotlinx.parcelize.Parcelize

interface TasksFeature {
    val tasksNavigationContract: NavigationContractApi<NoParams, UnitResult>
    val taskEditorNavigationContract: NavigationContractApi<TasksParams, TasksResult>


    @Parcelize
    data class TasksParams(
        val task: Task,
        val editorMode: EditorMode,
    ) : Parcelable

    sealed class TasksResult : Parcelable {

        @Parcelize
        object TaskCreated : TasksResult()

        @Parcelize
        object TaskEdited : TasksResult()

        @Parcelize
        object TaskDeleted : TasksResult()

        @Parcelize
        object Canceled : TasksResult()
    }
}