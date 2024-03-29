package io.fasthome.fenestram_messenger.util

import android.text.InputFilter
import android.text.Spanned

class SpacesToUnderscoresInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        return if (source.toString().contains(' '))
            source.toString().replace(' ', '_')
        else
            null
    }
}