package io.fasthome.fenestram_messenger.uikit.paging

import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult

private enum class PagesSource {
    NotSet,
    Service,
    Storage;
}

class EmptyCacheException : Exception("Failed to load initial page from service and storage is empty!")

fun <T : Any> cachingPagingSource(
    maxPageSize: Int,
    loadPageService: suspend (pageNumber: Int, pageSize: Int) -> List<T>,
    loadPageStorage: suspend (pageNumber: Int, pageSize: Int) -> List<T>,
    removeFromStorage: suspend () -> Unit,
    savePageStorage: suspend (List<T>) -> Unit,
): SuspendPagingSource<T> {

    var pagesSource = PagesSource.NotSet

    return SuspendPagingSource(
        maxPageSize = maxPageSize,
        loadPage = { pageNumber: Int, pageSize: Int ->
            val isInitialPage = pageNumber == 0

            suspend fun fromService() = callForResult {
                loadPageService(pageNumber, pageSize)
                    .also {
                        if (isInitialPage) {
                            removeFromStorage()
                        }
                        savePageStorage(it)
                    }
            }

            suspend fun fromStorage(): CallResult<List<T>> {
                val cached = loadPageStorage(pageNumber, pageSize)
                return if (isInitialPage && cached.isEmpty()) {
                    CallResult.Error(EmptyCacheException())
                } else {
                    CallResult.Success(cached)
                }
            }

            when (pagesSource) {
                PagesSource.NotSet -> when (val result = fromService()) {
                    is CallResult.Error -> {
                        pagesSource = PagesSource.Storage
                        fromStorage()
                    }
                    is CallResult.Success -> {
                        pagesSource = PagesSource.Service
                        result
                    }
                }
                PagesSource.Service -> fromService()
                PagesSource.Storage -> fromStorage()
            }
        }
    )
}