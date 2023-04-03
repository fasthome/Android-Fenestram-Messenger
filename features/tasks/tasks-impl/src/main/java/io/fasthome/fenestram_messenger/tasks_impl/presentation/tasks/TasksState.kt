package io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks

data class TasksState(
    val selectedTab: Tabs
)

enum class Tabs(val type: String) {
    SELF("self"),
    CONTROL("control"),
}