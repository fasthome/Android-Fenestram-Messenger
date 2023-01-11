package io.fasthome.fenestram_messenger.util.model

import android.text.InputFilter
import android.text.Spanned
import io.fasthome.fenestram_messenger.util.REGEX_RU_EN_LETTERS

class EditTextFilter(private val patten: Regex = REGEX_RU_EN_LETTERS) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        return if (source.toString().matches(patten))
            source
        else
            source?.dropLast(1)
    }
}