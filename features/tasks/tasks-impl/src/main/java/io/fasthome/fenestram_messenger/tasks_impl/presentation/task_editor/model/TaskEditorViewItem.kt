package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.model

import io.fasthome.fenestram_messenger.util.PrintableText

data class Style(
    val boxBackgroundColor: Int?,
    val boxStrokeColor: Int,
    val boxStrokeDash: Float,
    val calendarIcon: Int,
    val priorityStatusIcon: Int,
)

data class ExecutorViewItem(
    val name: PrintableText,
    val avatar: String?,
    val actionBackground: Int,
    val actionBackgroundColor: Int,
)