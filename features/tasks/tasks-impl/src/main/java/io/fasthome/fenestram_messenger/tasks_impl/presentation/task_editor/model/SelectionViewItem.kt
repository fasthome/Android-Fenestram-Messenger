package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.model

import io.fasthome.fenestram_messenger.util.PrintableText

data class SelectionViewItem(
    val selectionName: PrintableText,
    val selectionType: SelectionType,
    var isChecked: Boolean = false,
    val radioButtonVisible: Boolean = false,
    val arrowVisible: Boolean = false,
)

enum class SelectionType {
    EXECUTOR_SELF,
    EXECUTOR_OTHER,
    EXECUTOR_COMPANION,
    EXECUTOR_CHAT_USER,

    PRIORITY_LOWEST,
    PRIORITY_LOW,
    PRIORITY_MEDIUM,
    PRIORITY_HIGH,
    PRIORITY_HIGHEST,

    STATUS_QUEUE,
    STATUS_IN_WORK,
    STATUS_DONE,
}