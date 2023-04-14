package io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.model

import io.fasthome.fenestram_messenger.tasks_api.model.Task
import io.fasthome.fenestram_messenger.uikit.custom_view.task_card.ConfeeTaskCardState

sealed class TaskViewItem {

    data class TaskCardViewItem(val task: Task, val taskCardState: ConfeeTaskCardState) : TaskViewItem()

    data class Header(val createdAt: String) : TaskViewItem()
}