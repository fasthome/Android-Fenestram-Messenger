package io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.mapper

import io.fasthome.fenestram_messenger.tasks_api.mapper.TaskMapper
import io.fasthome.fenestram_messenger.tasks_api.model.Task
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.model.TaskViewItem

fun Task.toTaskViewItem(taskMapper: TaskMapper, selfUserId: Long): TaskViewItem {
    /*
    *  Заглушка из предположения о том, что список задач сортируется на сервере и дата приходит
    *  как Task(number = -1, createdAt = "сегодня/вчера/дата")
    */
    if (number == -1L) return TaskViewItem.Header(createdAt = createdAt)

    val taskCardState = taskMapper.mapToTaskCardViewItem(this, selfUserId)

    return TaskViewItem.TaskCardViewItem(task = this, taskCardState = taskCardState)
}
