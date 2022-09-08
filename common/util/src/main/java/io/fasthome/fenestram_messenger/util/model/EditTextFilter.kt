package io.fasthome.fenestram_messenger.util.model

import android.text.InputFilter
import android.text.Spanned

class EditTextFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        return if (source.toString().matches(Regex("[a-zA-Zа-яА-Я]+")))
            source
        else
            source?.dropLast(1)
    }
}