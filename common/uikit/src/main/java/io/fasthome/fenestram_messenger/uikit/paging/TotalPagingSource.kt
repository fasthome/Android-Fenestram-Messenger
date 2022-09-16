package io.fasthome.fenestram_messenger.uikit.paging

import androidx.paging.PagingSource
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

/**
 * [PagingSource] и общее кол-во элементов в списке ([totalCount]).
 *
 * @param totalCount общее кол-во элементов в списке. Данный [Deferred] завершится сразу перед тем,
 * как будет успешно загружена первая страница пагинации.
 */
class TotalPagingSource<Key : Any, Value : Any>(
    val pagingSource: PagingSource<Key, Value>,
    val totalCount: Deferred<Int>,
)


fun <T : Any> totalPagingSource(
    maxPageSize: Int,
    loadPageService: suspend (pageNumber: Int, pageSize: Int) -> ListWithTotal<T>
): TotalPagingSource<Int, T> {
    val totalCount = CompletableDeferred<Int>()

    val pagingSource = SuspendPagingSource(
        maxPageSize = maxPageSize,
        loadPage = { pageNumber, pageSize ->
            callForResult {
                loadPageService(pageNumber, pageSize)
                    .also { totalCount.complete(it.totalCount) }
                    .list
            }
        },
    )

    return TotalPagingSource(pagingSource = pagingSource, totalCount = totalCount)
}

/**
 * Можно применять для случаев, в которых требуется избегать лишних запросов, инициируемых пагинацией
 */
//fun <T : Any> emptyPagingSource() = totalPagingSource(maxPageSize = 1) { _, _ -> ListWithTotal<T>(emptyList(), 0) }
