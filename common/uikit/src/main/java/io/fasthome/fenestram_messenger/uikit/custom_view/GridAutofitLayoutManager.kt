package io.fasthome.fenestram_messenger.uikit.custom_view

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fasthome.fenestram_messenger.util.dp

class GridAutofitLayoutManager : GridLayoutManager {

    companion object {
        private const val DEFAULT_COLUMN_WIDTH = 80
        private const val MIN_SPAN_COUNT = 1
    }

    private var columnWidth = 0
    private var isColumnWidthChanged = true
    private var lastWidth = 0
    private var lastHeight = 0

    constructor(context: Context, columnWidth: Int) : super(context, MIN_SPAN_COUNT) {
        setColumnWidth(checkedColumnWidth(columnWidth))
    }

    constructor(
        context: Context,
        columnWidth: Int,
        orientation: Int,
        reverseLayout: Boolean,
    ) : super(context, 1, orientation, reverseLayout) {
        setColumnWidth(checkedColumnWidth(columnWidth))
    }

    private fun checkedColumnWidth(columnWidth: Int): Int {
        if (columnWidth <= 0) {
            return DEFAULT_COLUMN_WIDTH.dp
        }
        return columnWidth
    }

    fun setColumnWidth(newColumnWidth: Int) {
        if (newColumnWidth > 0 && newColumnWidth != columnWidth) {
            columnWidth = newColumnWidth
            isColumnWidthChanged = true
        }
    }

    override fun onLayoutChildren(
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?,
    ) {
        val width = width
        val height = height
        if (columnWidth > 0 && width > 0 && height > 0 && (isColumnWidthChanged || lastWidth != width || lastHeight != height)) {
            val totalSpace: Int = if (orientation == VERTICAL) {
                width - paddingRight - paddingLeft
            } else {
                height - paddingTop - paddingBottom
            }
            val spanCount = MIN_SPAN_COUNT.coerceAtLeast(totalSpace / columnWidth)
            setSpanCount(spanCount)
            isColumnWidthChanged = false
        }
        lastWidth = width
        lastHeight = height
        super.onLayoutChildren(recycler, state)
    }
}