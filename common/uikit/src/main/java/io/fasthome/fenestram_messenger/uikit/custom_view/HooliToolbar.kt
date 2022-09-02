/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.uikit.custom_view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.annotation.StyleableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import io.fasthome.fenestram_messenger.uikit.R
import io.fasthome.fenestram_messenger.uikit.databinding.ToolbarCommonBinding
import io.fasthome.fenestram_messenger.util.android.drawable
import io.fasthome.fenestram_messenger.util.onClick

class HooliToolbar : ConstraintLayout {

    private val binding: ToolbarCommonBinding

    var title: CharSequence?
        get() = binding.toolbarTitle.text
        set(value) {
            binding.toolbarTitle.text = value
        }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        binding = ToolbarCommonBinding.inflate(LayoutInflater.from(context), this, true)

        setupViews(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        binding = ToolbarCommonBinding.inflate(LayoutInflater.from(context), this, true)

        setupViews(attrs)
    }

    fun setOnButtonClickListener(onClick: () -> Unit) {
        binding.ibCancel.onClick {
            onClick()
        }
    }

    fun setButtonVisibility(visible: Boolean) {
        binding.ibCancel.isInvisible = !visible
    }

    private fun setupViews(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.HooliToolbar)

        title = a.getText(R.styleable.HooliToolbar_title)

        val startButtonDrawableId = setDrawable(a, binding.ibCancel, R.styleable.HooliToolbar_buttonDrawable)
        binding.ibCancel.isInvisible = startButtonDrawableId == 0

        a.recycle()
    }

    private fun setDrawable(a: TypedArray, target: ImageView, @StyleableRes attribute: Int): Int {
        val drawableId = a.getResourceId(attribute, 0)

        if (drawableId != 0) {
            val drawable = context.drawable(drawableId)
            target.setImageDrawable(drawable)
        } else {
            val drawable = context.drawable(R.drawable.ic_cancel)
            target.setImageDrawable(drawable)
            return R.drawable.ic_cancel
        }

        return drawableId
    }
}