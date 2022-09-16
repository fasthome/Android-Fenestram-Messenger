package io.fasthome.fenestram_messenger.uikit.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.fasthome.fenestram_messenger.util.CallResult

class SuspendPagingSource<T : Any>(
    private val maxPageSize: Int,
    private val loadPage: suspend (pageNumber: Int, pageSize: Int) -> CallResult<List<T>>
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        val anchorPos = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPos) ?: return null

        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val pageNumber = params.key ?: 0
        val pageSize = params.loadSize.coerceAtMost(maxPageSize)

        return when (val callResult = loadPage(pageNumber, pageSize)) {
            is CallResult.Success -> {
                val items = callResult.data
                val nextKey = if (items.size < pageSize) null else pageNumber + 1
                val previousKey = if (pageNumber == 0) null else pageNumber - 1
                LoadResult.Page(items, previousKey, nextKey)
            }
            is CallResult.Error -> {
                LoadResult.Error(callResult.error)
            }
        }
    }
}
