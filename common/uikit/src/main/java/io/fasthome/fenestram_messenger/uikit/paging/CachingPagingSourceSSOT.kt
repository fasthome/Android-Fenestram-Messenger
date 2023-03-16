package io.fasthome.fenestram_messenger.uikit.paging

import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult

fun <T : Any> cachingPagingSourceSSOT(
    maxPageSize: Int,
    loadPageService: suspend (pageNumber: Int, pageSize: Int) -> List<T>,
    loadPageStorage: suspend (pageNumber: Int, pageSize: Int) -> List<T>,
    removeFromStorage: suspend () -> Unit,
    savePageStorage: suspend (List<T>) -> Unit,
): SuspendPagingSource<T> {

    return SuspendPagingSource(
        maxPageSize = maxPageSize,
        loadPage = { pageNumber: Int, pageSize: Int ->
            suspend fun fromService() = callForResult {
                loadPageService(pageNumber, pageSize)
                    .also {
                        removeFromStorage()
                        savePageStorage(it)
                    }
            }

            suspend fun fromStorage(): CallResult<List<T>> {
                val cached = loadPageStorage(pageNumber, pageSize)
                return if (cached.isEmpty()) {
                    CallResult.Error(EmptyCacheException())
                } else {
                    CallResult.Success(cached)
                }
            }

            val storageRes = fromStorage()
            when(storageRes) {
                is CallResult.Error -> {
                    fromService()
                }
                is CallResult.Success -> {
                    storageRes
                }
            }
        }
    )
}