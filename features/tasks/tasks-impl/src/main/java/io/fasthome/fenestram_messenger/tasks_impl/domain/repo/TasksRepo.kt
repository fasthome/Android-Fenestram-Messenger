/**
 * Created by Vladimir Rudakov on 24.03.2023.
 */
package io.fasthome.fenestram_messenger.tasks_impl.domain.repo

import io.fasthome.fenestram_messenger.tasks_api.model.Task
import io.fasthome.fenestram_messenger.uikit.paging.TotalPagingSource
import io.fasthome.fenestram_messenger.util.CallResult


interface TasksRepo {
    fun getTasks(type: String): TotalPagingSource<Int, Task>

    suspend fun getTaskByNumber(taskNumber: Long): CallResult<Task>
}