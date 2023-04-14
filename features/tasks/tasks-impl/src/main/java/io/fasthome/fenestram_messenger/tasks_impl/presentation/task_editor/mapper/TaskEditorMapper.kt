package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.mapper

import io.fasthome.fenestram_messenger.tasks_api.mapper.TaskMapper
import io.fasthome.fenestram_messenger.tasks_api.model.EditorMode
import io.fasthome.fenestram_messenger.tasks_api.model.Task
import io.fasthome.fenestram_messenger.tasks_impl.R
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.TaskEditorState
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.model.ExecutorViewItem
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.model.SelectionType
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.model.Style
import io.fasthome.fenestram_messenger.uikit.custom_view.task_card.ConfeeTaskCardState
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.android.color

fun Task.mapToTaskEditorViewItem(editorMode: EditorMode, selfUserId: Long, taskMapper: TaskMapper): TaskEditorState {

    val modeText = when (editorMode) {
        EditorMode.VIEW -> {
            return mapViewMode(selfUserId, taskMapper)
        }
        EditorMode.EDIT -> {
            PrintableText.StringResource(R.string.task_editor_mode_edit)
        }
        EditorMode.CREATE -> {
            PrintableText.StringResource(R.string.task_editor_mode_create)
        }
    }

    return TaskEditorState(
        editorMode = editorMode,
        isEditMode = true,
        modeText = modeText,
        taskNumber = PrintableText.StringResource(R.string.task_card_other, taskMapper.mapTaskNumber(number)),
        createdAt = PrintableText.Raw(createdAt),
        originalMessageVisible = messageId != null,
        style = Style(
            boxBackgroundColor = taskMapper.appTheme?.bg01Color() ?: 0,
            boxStrokeColor = taskMapper.appTheme?.stroke1Color() ?: 0,
            boxStrokeDash = 0f,
            calendarIcon = R.drawable.ic_open_calendar,
            priorityStatusIcon = R.drawable.ic_action_next
        ),
        title = title,
        description = description ?: "",
        customerItem = ConfeeTaskCardState.UserViewItem(customer.name, customer.avatar),
        executorItem = ExecutorViewItem(
            if (executor.id != selfUserId) PrintableText.Raw(executor.name) else PrintableText.StringResource(R.string.task_editor_executor_self),
            if (executor.id != selfUserId) executor.avatar else null,
            R.drawable.ic_action_next,
            taskMapper.appTheme?.stroke1Color() ?: 0
        ),
        deadline = updatedAt ?: "",
        participants = participants?.map { ConfeeTaskCardState.UserViewItem(it.name, it.avatar) } ?: listOf(),
        priorityItem = taskMapper.mapToPriorityViewItem(priority),
        statusItem = taskMapper.mapToStatusViewItem(status),
    )
}

private fun Task.mapViewMode(selfUserId: Long, taskMapper: TaskMapper): TaskEditorState {
    return TaskEditorState(
        editorMode = EditorMode.VIEW,
        isEditMode = false,
        modeText = PrintableText.StringResource(R.string.task_editor_mode_view),
        taskNumber = PrintableText.StringResource(R.string.task_card_other, taskMapper.mapTaskNumber(number)),
        createdAt = PrintableText.Raw(createdAt),
        originalMessageVisible = messageId != null,
        style = Style(
            boxBackgroundColor = null,
            boxStrokeColor = taskMapper.appTheme?.context?.color(R.color.stroke_1_dark) ?: 0,
            boxStrokeDash = 5f,
            calendarIcon = 0,
            priorityStatusIcon = 0
        ),
        title = title,
        description = description ?: "",
        customerItem = ConfeeTaskCardState.UserViewItem(customer.name, customer.avatar),
        executorItem = ExecutorViewItem(
            if (executor.id != selfUserId) PrintableText.Raw(executor.name) else PrintableText.StringResource(R.string.task_editor_executor_self),
            if (executor.id != selfUserId) executor.avatar else null,
            R.drawable.ic_action_chat,
            taskMapper.appTheme?.mainActive() ?: 0
        ),
        deadline = updatedAt ?: "",
        participants = participants?.map { ConfeeTaskCardState.UserViewItem(it.name, it.avatar) } ?: listOf(),
        priorityItem = taskMapper.mapToPriorityViewItem(priority),
        statusItem = taskMapper.mapToStatusViewItem(status),
    )
}


fun SelectionType.mapToStatus() = when (this) {
    SelectionType.STATUS_QUEUE -> Task.Status.QUEUE
    SelectionType.STATUS_IN_WORK -> Task.Status.IN_WORK
    SelectionType.STATUS_DONE -> Task.Status.DONE
    else -> Task.Status.QUEUE
}

fun SelectionType.mapToPriority() = when (this) {
    SelectionType.PRIORITY_LOWEST -> Task.Priority.LOWEST
    SelectionType.PRIORITY_LOW -> Task.Priority.LOW
    SelectionType.PRIORITY_MEDIUM -> Task.Priority.MEDIUM
    SelectionType.PRIORITY_HIGH -> Task.Priority.HIGH
    SelectionType.PRIORITY_HIGHEST -> Task.Priority.HIGHEST
    else -> Task.Priority.MEDIUM
}