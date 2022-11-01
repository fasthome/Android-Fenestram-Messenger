package io.fasthome.fenestram_messenger.util.links

import android.graphics.Color
import android.graphics.Typeface
import java.util.regex.Pattern

@Suppress("MemberVisibilityCanBePrivate")
class Link {

    @JvmField
    var text: String? = null

    @JvmField
    var pattern: Pattern? = null

    @JvmField
    var textColor = 0

    @JvmField
    var textColorOfHighlightedLink = 0

    @JvmField
    var highlightAlpha = DEFAULT_ALPHA

    @JvmField
    var underlined = true

    @JvmField
    var bold = false

    @JvmField
    var typeface: Typeface? = null

    @JvmField
    var clickListener: OnClickListener? = null

    @JvmField
    var longClickListener: OnLongClickListener? = null

    constructor(link: Link) {
        this.text = link.text
        this.pattern = link.pattern
        this.clickListener = link.clickListener
        this.longClickListener = link.longClickListener
        this.textColor = link.textColor
        this.textColorOfHighlightedLink = link.textColorOfHighlightedLink
        this.highlightAlpha = link.highlightAlpha
        this.underlined = link.underlined
        this.bold = link.bold
        this.typeface = link.typeface
    }

    constructor(text: String) {
        this.text = text
        this.pattern = null
    }

    constructor(pattern: Pattern) {
        this.pattern = pattern
        this.text = null
    }

    fun setText(text: String): Link {
        this.text = text
        this.pattern = null
        return this
    }

    fun setPattern(pattern: Pattern): Link {
        this.pattern = pattern
        this.text = null
        return this
    }

    fun setOnClickListener(clickListener: OnClickListener): Link {
        this.clickListener = clickListener
        return this
    }

    fun setOnClickListener(listener: (String) -> Unit): Link {
        this.clickListener = object : OnClickListener {
            override fun onClick(clickedText: String) {
                listener(clickedText)
            }
        }
        return this
    }

    fun setOnLongClickListener(longClickListener: OnLongClickListener): Link {
        this.longClickListener = longClickListener
        return this
    }

    fun setOnLongClickListener(listener: (String) -> Unit): Link {
        this.longClickListener = object : OnLongClickListener {
            override fun onLongClick(clickedText: String) {
                listener(clickedText)
            }
        }
        return this
    }

    fun setTextColor(color: Int): Link {
        this.textColor = color
        return this
    }

    fun setTextColorOfHighlightedLink(colorOfHighlightedLink: Int): Link {
        this.textColorOfHighlightedLink = colorOfHighlightedLink
        return this
    }

    fun setUnderlined(underlined: Boolean): Link {
        this.underlined = underlined
        return this
    }

    fun setBold(bold: Boolean): Link {
        this.bold = bold
        return this
    }

    fun setHighlightAlpha(alpha: Float): Link {
        this.highlightAlpha = alpha
        return this
    }

    fun setTypeface(typeface: Typeface): Link {
        this.typeface = typeface
        return this
    }

    fun interface OnClickListener {
        fun onClick(clickedText: String)
    }

    fun interface OnLongClickListener {
        fun onLongClick(clickedText: String)
    }

    companion object {
        val DEFAULT_COLOR = Color.parseColor("#5EA4FF")
        private const val DEFAULT_ALPHA = .15f
    }
}