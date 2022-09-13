package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.Locale

class PhoneNumberInputManager(
    private val phoneNumberUtil: PhoneNumberUtil,
    private val editText: EditText,
    val setCountryName: (String) -> Unit
) : TextWatcher {

    override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(editable: Editable?) {
        var number = editable.toString()
        if (!number.startsWith("+")) {
            number = "+${number.replace("+", "")}"
            setCountryName("")
            editText.setText(number)
            Selection.setSelection(editText.text, 1)
        }

        val countryIsoCode = getCountryIsoCode(number)

        if (number.filter { it.isDigit() }.length <= 3) {
            return setCountryName("")
        } else {
            val country = countryIsoCode?.let {
                Locale("", it).displayCountry
            } ?: "Некорректный код страны"
            setCountryName(country)
        }
    }

    private fun getCountryIsoCode(number: String): String? {
        val phoneNumber = parseNumber(number) ?: return null
        return phoneNumberUtil.getRegionCodeForCountryCode(phoneNumber.countryCode)
    }

    fun validateNumber(number: String): Boolean {
        val phoneNumber = parseNumber(number) ?: return false
        return phoneNumberUtil.isPossibleNumberForType(
            phoneNumber, PhoneNumberUtil.PhoneNumberType.MOBILE
        )
    }

    private fun parseNumber(number: String) = try {
        phoneNumberUtil.parse(number, null)
    } catch (e: NumberParseException) {
        Log.e("PhoneNumberManager", "error during parsing a number")
        null
    }
}