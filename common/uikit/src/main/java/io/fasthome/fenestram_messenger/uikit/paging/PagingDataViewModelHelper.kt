package io.fasthome.fenestram_messenger.uikit.paging

import androidx.paging.*
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.getOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PagingDataViewModelHelper {
    private var source: TotalPagingSource<*, *>? = null
    var messageActionChatId: Long = -1

    companion object {
        const val PAGE_SIZE = 50
    }

    fun <K : Any, T : Any> getDataFlow(
        getItems: () -> TotalPagingSource<Int, K>,
        getCachedSelectedId: suspend () -> Long?,
        getItem: suspend (itemId: Long) -> CallResult<K>?,
        mapDataItem: (K, Long) -> T,
        getItemId: (K) -> Long?
    ): Flow<PagingData<T>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = {
                getItems()
                    .also { source = it }
                    .pagingSource
            },
        )
            .flow
            .map {
                /**
                 * Выбранный элемент должен быть в начале списка. Поэтому сначала удаляем его из полученных с сервера
                 * данных, потом добавляем в начало
                 */

                val selectedItemId = getCachedSelectedId() ?: return@map it

                it
                    .filter { item ->
                        getItemId(item) != selectedItemId
                    }
                    .insertSeparators { before: K?, after: K? ->
                        /**
                         * Добавляем в начало списка выбранный элемент, если список не пуст
                         */

                        if (before == null && after == null) return@insertSeparators null
                        if (before != null) return@insertSeparators null
                        getItem(selectedItemId)?.getOrNull()
                    }

            }
            .map { pagingData ->
                pagingData.map {
                    mapDataItem(it, messageActionChatId)
                }
            }

    fun invalidateSource() = source?.pagingSource?.invalidate()

}