package io.fasthome.fenestram_messenger.uikit.custom_view.bottomNav

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.Nullable
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView


class HooliBottomNavigation : BottomNavigationView {
    private var tabIndicator: TabIndicator = TabIndicator(context)


    init {
        addView(tabIndicator)
        setOnItemSelectedListener {
            val itemView = findViewById<BottomNavigationItemView>(it.itemId)
            tabIndicator.setNewPosition(itemView, true)
            return@setOnItemSelectedListener true
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        tabIndicator.post {
            val menu = menu.getItem(0)
            val itemView = findViewById<BottomNavigationItemView>(menu.itemId)
            tabIndicator.setNewPosition(itemView, false)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(
        context, attrs
    ) {
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyle: Int) : super(
        context, attrs, defStyle
    ) {
    }

}