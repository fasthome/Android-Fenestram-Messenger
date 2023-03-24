package io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.tasks_impl.domain.logic.TasksInteractor
import io.fasthome.fenestram_messenger.util.PrintableText

class TasksViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val tasksInteractor: TasksInteractor,
) : BaseViewModel<TasksState, TasksEvent>(router, requestParams) {

    override fun createInitialState(): TasksState {
        return TasksState(
            taskName = PrintableText.EMPTY
        )
    }

}