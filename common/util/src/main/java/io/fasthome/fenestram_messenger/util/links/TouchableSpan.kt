package io.fasthome.fenestram_messenger.util.links

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.TextPaint
import android.util.TypedValue
import android.view.View
import io.fasthome.fenestram_messenger.util.R
import io.fasthome.fenestram_messenger.util.links.Link.Companion.DEFAULT_ALPHA
import kotlin.math.roundToInt

class TouchableSpan(context: Context, private val link: Link) : TouchableBaseSpan() {

    private var textColor: Int = 0
    private var textColorOfHighlightedLink: Int = 0

    init {
        this.textColor = getDefaultColor(context, R.styleable.LinkBuilder_defaultLinkColor)

        this.textColorOfHighlightedLink =
            getDefaultColor(context, R.styleable.LinkBuilder_defaultTextColorOfHighlightedLink)

        if (this.textColorOfHighlightedLink == Link.DEFAULT_COLOR) {
            this.textColorOfHighlightedLink = textColor
        }
    }

    private fun getDefaultColor(context: Context, index: Int): Int {
        val array = obtainStyledAttrsFromThemeAttr(
            context,
            R.attr.linkBuilderStyle,
            R.styleable.LinkBuilder
        )
        val color = array.getColor(index, Link.DEFAULT_COLOR)
        array.recycle()

        return color
    }

    override fun onClick(widget: View) {
        if (link.text != null) {
            link.clickListener?.invoke(link.text!!)
        }

        super.onClick(widget)
    }

    override fun onLongClick(widget: View) {
        if (link.text != null) {
            link.longClickListener?.invoke(link.text!!)
        }

        super.onLongClick(widget)
    }

    private fun adjustAlpha(color: Int): Int {
        val alpha = (Color.alpha(color) * DEFAULT_ALPHA).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        return Color.argb(alpha, red, green, blue)
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)

        ds.isUnderlineText = link.underlined
        ds.color = if (isTouched) textColorOfHighlightedLink else textColor
        ds.bgColor = if (isTouched) adjustAlpha(textColor) else Color.TRANSPARENT
    }

    companion object {

        private fun obtainStyledAttrsFromThemeAttr(
            context: Context,
            themeAttr: Int,
            styleAttrs: IntArray
        ): TypedArray {
            val outValue = TypedValue()
            context.theme.resolveAttribute(themeAttr, outValue, true)
            val styleResId = outValue.resourceId

            return context.obtainStyledAttributes(styleResId, styleAttrs)
        }
    }
}