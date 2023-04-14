package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor

import io.fasthome.fenestram_messenger.tasks_api.model.EditorMode
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.model.ExecutorViewItem
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.model.Style
import io.fasthome.fenestram_messenger.uikit.custom_view.task_card.ConfeeTaskCardState
import io.fasthome.fenestram_messenger.util.PrintableText

data class TaskEditorState(
    val editorMode: EditorMode,
    val isEditMode: Boolean,

    val modeText: PrintableText,
    val taskNumber: PrintableText,
    val createdAt: PrintableText,
    val originalMessageVisible: Boolean,
    val style: Style,

    val title: String,
    val description: String,
    val deadline: String,

    val customerItem: ConfeeTaskCardState.UserViewItem,
    val executorItem: ExecutorViewItem,
    val participants: List<ConfeeTaskCardState.UserViewItem>,
    val priorityItem: ConfeeTaskCardState.PriorityViewItem,
    val statusItem: ConfeeTaskCardState.StatusViewItem,
)
