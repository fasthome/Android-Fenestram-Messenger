package io.fasthome.fenestram_messenger.tasks_impl.domain.logic

import io.fasthome.fenestram_messenger.tasks_api.model.Task
import io.fasthome.fenestram_messenger.tasks_impl.domain.repo.TasksRepo
import io.fasthome.fenestram_messenger.uikit.paging.TotalPagingSource

class TasksInteractor(private val tasksRepo: TasksRepo) {
    fun getTasks(type: String): TotalPagingSource<Int, Task> = tasksRepo.getTasks(type)

    suspend fun getTaskByNumber(taskNumber: Long) = tasksRepo.getTaskByNumber(taskNumber)

}