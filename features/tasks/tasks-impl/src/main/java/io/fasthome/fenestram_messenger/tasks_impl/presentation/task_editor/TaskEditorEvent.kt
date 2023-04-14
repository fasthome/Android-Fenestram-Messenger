package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor

import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.model.SelectionViewItem
import io.fasthome.fenestram_messenger.util.PrintableText

sealed interface TaskEditorEvent {

    object ClearTitle : TaskEditorEvent
    object ClearDescription : TaskEditorEvent

    class ShowSelectionDialog(val selectionTitle: PrintableText, val items: List<SelectionViewItem>) : TaskEditorEvent
    class ShowActionMenu(val isEditEnabled: Boolean) : TaskEditorEvent

    class ToggleReadyButton(val isEnabled: Boolean) : TaskEditorEvent

}

