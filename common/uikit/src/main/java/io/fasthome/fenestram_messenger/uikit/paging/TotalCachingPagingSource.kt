package io.fasthome.fenestram_messenger.uikit.paging

import kotlinx.coroutines.CompletableDeferred

fun <T : Any> totalCachingPagingSource(
    maxPageSize: Int,
    loadPageService: suspend (pageNumber: Int, pageSize: Int) -> ListWithTotal<T>,
    loadPageStorage: suspend (pageNumber: Int, pageSize: Int) -> List<T>,
    loadTotalCountStorage: suspend () -> Int,
    removeFromStorage: suspend () -> Unit,
    savePageStorage: suspend (List<T>) -> Unit,
): TotalPagingSource<Int, T> {
    val totalCount = CompletableDeferred<Int>()

    val pagingSource = cachingPagingSource(
        maxPageSize = maxPageSize,
        loadPageService = { pageNumber, pageSize ->
            loadPageService(pageNumber, pageSize)
                .also { totalCount.complete(it.totalCount) }
                .list
        },
        loadPageStorage = { pageNumber, pageSize ->
            if (!totalCount.isCompleted) {
                totalCount.complete(loadTotalCountStorage())
            }
            loadPageStorage(pageNumber, pageSize)
        },
        removeFromStorage = removeFromStorage,
        savePageStorage = savePageStorage,
    )

    return TotalPagingSource(pagingSource = pagingSource, totalCount = totalCount)
}