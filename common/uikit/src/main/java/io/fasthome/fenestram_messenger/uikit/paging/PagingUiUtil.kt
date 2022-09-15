package io.fasthome.fenestram_messenger.uikit.paging

import android.view.View
import androidx.core.view.isVisible
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

object PagingUiUtil {

    /**
     * @param isListEmpty - пуст ли список
     * @param isDefaultSearch - к списку не применены никакие фильтры
     * @param isFloatingRecycler - нужно ли оставлять список видимым во время загрузки (1)
     * (1) Применяется в случае, когда поисковое поле - часть списка
     * */

    fun renderLoadState(
        emptyListView: View,
        errorView: View?,
        progressView: View,
        recyclerView: View,

        swipeRefresh: SwipeRefreshLayout? = null,

        loadState: LoadState,
        isListEmpty: Boolean,
        errorListener: ((Throwable) -> Unit)? = null,
        isDefaultSearch: Boolean = true,
        isFloatingRecycler: Boolean = false,
    ) {
        listOf(errorView, progressView, emptyListView).forEach { it?.isVisible = false }
        if (!isFloatingRecycler) recyclerView.isVisible = false

        val (visibleView, isSwipeRefreshVisible) = when {
            loadState is LoadState.Error -> {
                errorListener?.invoke(loadState.error)
                errorView to false
            }
            loadState == LoadState.Loading && isListEmpty -> progressView to isFloatingRecycler
            else -> {
                val showEmptyListView = isListEmpty && isDefaultSearch
                val visibleView = if (showEmptyListView) {
                    emptyListView
                } else {
                    recyclerView
                }

                swipeRefresh?.isRefreshing = loadState == LoadState.Loading
                visibleView to true
            }
        }

        visibleView?.isVisible = true
        swipeRefresh?.isVisible = isSwipeRefreshVisible
    }

    suspend fun addScrollToTopOnRefreshBehavior(
        adapter: PagerDelegateAdapter<*>,
        recyclerView: RecyclerView,
    ) {
        adapter.loadStateFlow
            .distinctUntilChangedBy(CombinedLoadStates::refresh)
            .filter { it.refresh is LoadState.NotLoading }
            .collect {
                delay(300)
                recyclerView.scrollToPosition(0)
            }
    }
}