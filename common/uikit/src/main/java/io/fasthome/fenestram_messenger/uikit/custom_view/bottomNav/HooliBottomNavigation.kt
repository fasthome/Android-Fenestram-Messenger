package io.fasthome.fenestram_messenger.uikit.custom_view.bottomNav

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.Nullable
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView


class HooliBottomNavigation : BottomNavigationView {

    private var tabIndicator: TabIndicator = TabIndicator(context)

    private var itemClickListener : OnItemSelectedListener? = null

    init {
        addView(tabIndicator)
        setOnItemSelectedListener {
            val itemView = findViewById<BottomNavigationItemView>(it.itemId)
            tabIndicator.setNewPosition(itemView, true)
            itemClickListener?.onNavigationItemSelected(it)
            return@setOnItemSelectedListener true
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        tabIndicator.post {
            val selectedMenu = menu.findItem(selectedItemId)
            val itemView = findViewById<BottomNavigationItemView>(selectedMenu.itemId)
            tabIndicator.setNewPosition(itemView, true)
        }
    }

    fun setItemClickListener(itemClickListener : OnItemSelectedListener){
        this.itemClickListener = itemClickListener
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
    )

    companion object {
        private const val defaultIndicatorPosition = 1
    }

}