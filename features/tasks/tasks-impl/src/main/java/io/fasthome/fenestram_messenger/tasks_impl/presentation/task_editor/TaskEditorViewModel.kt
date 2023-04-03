package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.tasks_impl.domain.logic.TasksInteractor
import io.fasthome.fenestram_messenger.util.PrintableText

class TaskEditorViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val tasksInteractor: TasksInteractor,
) : BaseViewModel<TaskEditorState, TaskEditorEvent>(router, requestParams) {

    override fun createInitialState(): TaskEditorState {
        return TaskEditorState(
            taskName = PrintableText.EMPTY
        )
    }

}