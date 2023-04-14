package io.fasthome.fenestram_messenger.util.android

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils


class RegexInputFilter(
    private val regex: Regex,
    private val keepPartialMatch: Boolean = false,
) : InputFilter {

    override fun filter(
        source: CharSequence, start: Int, end: Int,
        dest: Spanned, dstart: Int, dend: Int
    ): CharSequence? {
        if (!keepPartialMatch && !source.all(this::isCharAllowed)) return ""

        var keepOriginal = true
        val stringBuilder = StringBuilder(end - start)

        source.forEach { c ->
            if (isCharAllowed(c)) {
                stringBuilder.append(c)
            } else {
                keepOriginal = false
            }
        }
        return when {
            keepOriginal -> null
            source is Spanned -> {
                val spannableString = SpannableString(stringBuilder)
                TextUtils.copySpansFrom(
                    source,
                    start,
                    stringBuilder.length,
                    null,
                    spannableString,
                    0
                )
                spannableString
            }
            else -> stringBuilder
        }
    }

    private fun isCharAllowed(c: Char): Boolean = c.toString().matches(regex)
}