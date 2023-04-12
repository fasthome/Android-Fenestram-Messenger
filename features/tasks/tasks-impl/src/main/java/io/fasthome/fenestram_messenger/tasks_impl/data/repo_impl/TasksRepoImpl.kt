/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.tasks_impl.data.repo_impl

import io.fasthome.fenestram_messenger.tasks_api.model.Task
import io.fasthome.fenestram_messenger.tasks_impl.data.service.TasksService
import io.fasthome.fenestram_messenger.tasks_impl.data.storage.MockTasksStorage
import io.fasthome.fenestram_messenger.tasks_impl.domain.repo.TasksRepo
import io.fasthome.fenestram_messenger.uikit.paging.ListWithTotal
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper.Companion.PAGE_SIZE
import io.fasthome.fenestram_messenger.uikit.paging.TotalPagingSource
import io.fasthome.fenestram_messenger.uikit.paging.totalPagingSource
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult


class TasksRepoImpl(
    private val tasksService: TasksService,
    private val mockTasksStorage: MockTasksStorage
) : TasksRepo {
    override fun getTasks(type: String): TotalPagingSource<Int, Task> {
        return totalPagingSource(
            maxPageSize = PAGE_SIZE,
            loadPageService = { pageNumber, pageSize ->
                val list = mockTasksStorage.getTasks(type = type)
                return@totalPagingSource ListWithTotal(list, pageSize)
            },
        )
    }

    override suspend fun getTaskByNumber(taskNumber: Long): CallResult<Task> = callForResult {
        requireNotNull(mockTasksStorage.getTaskByNumber(taskNumber))
    }

}