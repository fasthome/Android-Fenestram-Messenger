package io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks

import io.fasthome.fenestram_messenger.tasks_api.model.Task

sealed interface TasksEvent {

    class ShowTaskMenu(val task: Task) : TasksEvent

}