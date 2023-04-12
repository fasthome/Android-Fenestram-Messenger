package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.mapper

import io.fasthome.fenestram_messenger.tasks_api.mapper.TaskMapper
import io.fasthome.fenestram_messenger.tasks_api.model.EditorMode
import io.fasthome.fenestram_messenger.tasks_api.model.Task
import io.fasthome.fenestram_messenger.tasks_impl.R
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.TaskEditorState
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.model.SelectionType
import io.fasthome.fenestram_messenger.uikit.custom_view.task_card.ConfeeTaskCardState
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.android.color
import java.util.*

fun Task.mapToTaskEditorState(editorMode: EditorMode, selfUserId: Long, taskMapper: TaskMapper?): TaskEditorState {

    var historyVisible = false
    var actionMenuVisible = true
    var createdAtLabelVisible = true
    var textBackgroundColor: Int? = taskMapper?.appTheme?.bg01Color()
    var textStrokeDash = 0f
    var textStrokeColor = taskMapper?.appTheme?.stroke1Color()
    var executorActionBackground = R.drawable.ic_action_next
    var executorActionBackgroundColor = taskMapper?.appTheme?.stroke1Color() ?: R.color.main_active
    var calendarIcon: Int = R.drawable.ic_open_calendar
    var priorityIcon: Int = R.drawable.ic_action_next

    val modeText = when (editorMode) {
        EditorMode.VIEW -> {
            historyVisible = true
            textBackgroundColor = null
            textStrokeDash = 5f
            textStrokeColor = taskMapper?.appTheme?.context?.color(R.color.stroke_1_dark)
            executorActionBackground = R.drawable.ic_action_chat
            executorActionBackgroundColor = taskMapper?.appTheme?.mainActive() ?: 0
            calendarIcon = 0
            priorityIcon = 0
            PrintableText.StringResource(R.string.task_editor_view_mode)
        }
        EditorMode.EDIT -> {
            PrintableText.StringResource(R.string.task_editor_edit_mode)
        }
        EditorMode.CREATE -> {
            actionMenuVisible = false
            createdAtLabelVisible = false
            PrintableText.StringResource(R.string.task_editor_create_mode)
        }
    }

    val taskNumber = if (number != null) ": # " + number.toString().padStart(6, '0') else ""
    var executorAvatar: String? = executor.avatar
    var executorName: PrintableText = PrintableText.Raw(executor.name)
    if (executor.id == selfUserId) {
        executorAvatar = null
        executorName = PrintableText.StringResource(R.string.task_editor_executor_self)
    }

    return TaskEditorState(
        isEditMode = editorMode != EditorMode.VIEW,

        modeText = modeText,
        taskNumber = PrintableText.StringResource(R.string.task_editor_task_number, taskNumber),
        createdAt = PrintableText.Raw(createdAt),
        historyVisible = historyVisible,
        actionMenuVisible = actionMenuVisible,
        createdAtLabelVisible = createdAtLabelVisible,
        originalMessageVisible = messageId != null,

        textBackgroundColor = textBackgroundColor,
        textStrokeColor = textStrokeColor ?: R.color.stroke_1,
        textStrokeDash = textStrokeDash,

        title = title,
        description = description ?: "",

        customerName = customer.name,
        customerAvatar = customer.avatar,
        executorName = executorName,
        executorAvatar = executorAvatar,
        executorActionBackground = executorActionBackground,
        executorActionBackgroundColor = executorActionBackgroundColor,

        deadline = updatedAt ?: "",
        calendarIcon = calendarIcon,

        participants = participants?.map { ConfeeTaskCardState.UserViewItem(it.name, it.avatar) } ?: listOf(),

        priority = priority.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
        priorityColor = taskMapper?.getPriorityStrokeColor(priority) ?: 0,
        statusItem = taskMapper?.mapToStatusViewItem(status) ?: ConfeeTaskCardState.StatusViewItem(
            PrintableText.EMPTY,
            0
        ),
        buttonsIcon = priorityIcon,


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