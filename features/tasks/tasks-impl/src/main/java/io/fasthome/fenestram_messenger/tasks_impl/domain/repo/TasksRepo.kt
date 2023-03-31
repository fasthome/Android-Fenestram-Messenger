/**
 * Created by Vladimir Rudakov on 24.03.2023.
 */
package io.fasthome.fenestram_messenger.tasks_impl.domain.repo

import io.fasthome.fenestram_messenger.tasks_api.model.Task
import io.fasthome.fenestram_messenger.uikit.paging.TotalPagingSource


interface TasksRepo {
    fun getTasks(type: String): TotalPagingSource<Int, Task>
}