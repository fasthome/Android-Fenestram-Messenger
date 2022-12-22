package io.fasthome.component.personality_data

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import androidx.core.text.trimmedLength

class SpaceInputFilter(
    private val regex: Regex,
) : InputFilter {

    override fun filter(
        source: CharSequence, start: Int, end: Int,
        dest: Spanned, dstart: Int, dend: Int
    ): CharSequence? {
        var keepOriginal = true
        val stringBuilder = StringBuilder(end - start)

        source.forEachIndexed { index, c ->
            if (isCharAllowed(c)) {
                if (dest.isNotEmpty() && c == ' ' && dend == 0) {
                    keepOriginal = false
                } else {
                    stringBuilder.append(c)
                }
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