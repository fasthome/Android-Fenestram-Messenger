package io.fasthome.fenestram_messenger.util.links

import android.graphics.Color
import java.util.regex.Pattern

class Link {

    @JvmField
    var text: String? = null

    @JvmField
    var pattern: Pattern? = null

    @JvmField
    var underlined = true

    @JvmField
    var clickListener: ((clickedText: String) -> Unit)? = null

    @JvmField
    var longClickListener: ((clickedText: String) -> Unit)? = null

    constructor(link: Link) {
        this.text = link.text
        this.pattern = link.pattern
        this.clickListener = link.clickListener
        this.longClickListener = link.longClickListener
        this.underlined = link.underlined
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

    fun setOnClickListener(clickListener: (clickedText: String) -> Unit): Link {
        this.clickListener = clickListener
        return this
    }

    fun setOnLongClickListener(longClickListener: (clickedText: String) -> Unit): Link {
        this.longClickListener = longClickListener
        return this
    }

    fun setUnderlined(underlined: Boolean): Link {
        this.underlined = underlined
        return this
    }

    companion object {
        val DEFAULT_COLOR = Color.parseColor("#5EA4FF")
        const val DEFAULT_ALPHA = .15f
    }
}