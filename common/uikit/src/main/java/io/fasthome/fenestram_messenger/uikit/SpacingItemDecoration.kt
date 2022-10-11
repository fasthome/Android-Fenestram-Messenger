package io.fasthome.fenestram_messenger.uikit

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SpacingItemDecoration(
    private val getSpacing: (index: Int, itemCount: Int) -> Rect,
) : ItemDecoration() {

    constructor(
        left: Int = 0,
        top: Int = 0,
        right: Int = 0,
        bottom: Int = 0,
    ) : this(getSpacing = Rect(left, top, right, bottom).let { r -> { _, _ -> r } })

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val index = parent.getChildAdapterPosition(view)
        if (index < 0) {
            outRect.setEmpty()
            return
        }
        val itemCount = state.itemCount
        outRect.set(getSpacing(index, itemCount))
    }
}