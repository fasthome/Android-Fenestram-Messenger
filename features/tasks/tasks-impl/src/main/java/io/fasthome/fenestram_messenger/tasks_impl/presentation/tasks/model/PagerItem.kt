package io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.model

import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.adapter.TaskAdapter
import io.fasthome.fenestram_messenger.util.PrintableText

data class PagerItem(val taskAdapter: TaskAdapter, val emptyText: PrintableText)