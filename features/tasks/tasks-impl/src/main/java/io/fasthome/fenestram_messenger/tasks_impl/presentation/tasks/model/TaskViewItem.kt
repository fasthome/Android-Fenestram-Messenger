package io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.model

import io.fasthome.fenestram_messenger.uikit.custom_view.task_card.ConfeeTaskCardState

sealed class TaskViewItem {

    data class TaskCardViewItem(val taskCardState: ConfeeTaskCardState) : TaskViewItem()

    data class Header(val createdAt: String) : TaskViewItem()
}