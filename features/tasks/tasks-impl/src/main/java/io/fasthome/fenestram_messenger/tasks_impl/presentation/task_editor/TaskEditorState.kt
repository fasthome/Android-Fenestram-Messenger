package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor

import io.fasthome.fenestram_messenger.uikit.custom_view.task_card.ConfeeTaskCardState
import io.fasthome.fenestram_messenger.util.PrintableText

data class TaskEditorState(
    val isEditMode: Boolean,

    val modeText: PrintableText,
    val taskNumber: PrintableText,
    val createdAt: PrintableText,
    val historyVisible: Boolean,
    val actionMenuVisible: Boolean,
    val createdAtLabelVisible: Boolean,
    val originalMessageVisible: Boolean,

    val textBackgroundColor: Int?,
    val textStrokeColor: Int,
    val textStrokeDash: Float,

    val title: String,
    val description: String,

    val customerName: String,
    val customerAvatar: String,
    val executorName: PrintableText,
    val executorAvatar: String?,
    val executorActionBackground: Int,
    val executorActionBackgroundColor: Int,
    val participants: List<ConfeeTaskCardState.UserViewItem>,

    val deadline: String,
    val calendarIcon: Int,
    val priority: String,
    val priorityColor: Int,
    val statusItem: ConfeeTaskCardState.StatusViewItem,
    val buttonsIcon: Int,
)

