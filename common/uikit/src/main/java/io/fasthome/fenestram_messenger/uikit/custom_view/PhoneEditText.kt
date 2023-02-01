package io.fasthome.fenestram_messenger.uikit.custom_view

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.*
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import io.fasthome.fenestram_messenger.uikit.R
import io.fasthome.fenestram_messenger.uikit.databinding.PhoneEditTextBinding
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.*

class PhoneEditText : ConstraintLayout {

    private val binding: PhoneEditTextBinding

    private val phoneNumberUtil = PhoneNumberUtil.createInstance(context)
    private val phoneNumberInputManager = PhoneNumberInputManager()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
        0
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.phone_edit_text, this, true)
        binding = PhoneEditTextBinding.bind(this)
        setupFilters()
    }

    private fun setupFilters() {
        with(binding.phoneInput) {
            binding.phoneInput.filters = arrayOf(PhoneNumberInputFilter(), InputFilter.LengthFilter(16))
            addTextChangedListener(phoneNumberInputManager)
            addTextChangedListener(PhoneNumberFormattingTextWatcher())
        }
    }

    fun getPhoneNumberFiltered() = binding.phoneInput.text.toString().filter { it.isDigit() }
    fun getPhoneNumber() = binding.phoneInput.text.toString()
    fun setPhoneNumber(phoneNumber: String) {
        binding.phoneInput.setText(phoneNumber)
    }

    fun isValid() = phoneNumberInputManager.validateNumber(getPhoneNumber())

    fun addListener(action: () -> Unit) {
        binding.phoneInput.addTextChangedListener {
            action()
        }
    }

    fun setBackground(resId: Int) {
        binding.phoneInput.setBackgroundResource(resId)
    }

    fun setBackgroundDrawable(@DrawableRes resId: Int) {
        binding.phoneInput.setBackgroundResource(resId)
    }

    fun setErrorLabelVisibility(isVisible: Boolean) = with(binding) {
        if (countryName.text != resources.getString(R.string.common_phone_number_country_error)) {
            errorLabel.isVisible = isVisible
        }
    }

    inner class PhoneNumberInputManager : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            var number = editable.toString()
            if (!number.startsWith("+")) {
                number = "+${number.replace("+", "")}"
                binding.countryName.text = ""
                binding.phoneInput.setText(number)
                Selection.setSelection(binding.phoneInput.text, 1)
            }

            val countryIsoCode = getCountryIsoCode(number)

            if (number.filter { it.isDigit() }.length <= 3) {
                binding.countryName.text = ""
            } else {
                val country = countryIsoCode?.let {
                    Locale("", it).displayCountry
                } ?: resources.getString(R.string.common_phone_number_country_error)
                binding.countryName.text = country
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

    class PhoneNumberInputFilter : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            return if (source.toString().matches(Regex("^[\\+]?[\\d\\s\\-]*"))
            )
                source
            else
                source?.dropLast(1)
        }
    }

}