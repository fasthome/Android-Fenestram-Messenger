package io.fasthome.fenestram_messenger.util

import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.text.color

fun TextView.spannableString(normalText : String, spannableText: String, @ColorInt color: Int) {
    val customizedString = SpannableStringBuilder()
        .append(normalText)
        .color(color) { append(spannableText) }
    this.text = customizedString
}