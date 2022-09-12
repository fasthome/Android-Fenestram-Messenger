package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import android.text.InputFilter
import android.text.Spanned

class PhoneNumberInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        return if (source.toString().matches(Regex("^[\\+]?[\\d\\s\\-]*")))
            source
        else
            source?.dropLast(1)
    }
}