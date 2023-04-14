package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.tasks_api.model.EditorMode
import io.fasthome.fenestram_messenger.tasks_api.model.Task
import kotlinx.parcelize.Parcelize

object TaskEditorNavigationContract :
    NavigationContract<TaskEditorNavigationContract.Params, TaskEditorNavigationContract.Result>(
        TaskEditorFragment::class
    ) {

    @Parcelize
    data class Params(
        val task: Task,
        val mode: EditorMode,
    ) : Parcelable

    sealed class Result : Parcelable {

        @Parcelize
        object TaskCreated : Result()

        @Parcelize
        object TaskEdited : Result()

        @Parcelize
        object TaskDeleted : Result()

        @Parcelize
        object Canceled : Result()
    }

}