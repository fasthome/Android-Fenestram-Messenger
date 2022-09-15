package io.fasthome.fenestram_messenger.uikit.custom_view

import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date

class HooliDatePicker(val textView: TextView) {

    private val constraintsBuilder =
        CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now()).build()
    private val datePicker =
        MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraintsBuilder).build()

    init {
        datePicker.addOnPositiveButtonClickListener {
            val date = SimpleDateFormat(DATE_PATTERN).format(Date(it))
            textView.text = date
        }
        datePicker.addOnNegativeButtonClickListener {
            textView.text = null
        }
        datePicker.addOnCancelListener {
            textView.text = null
        }
    }

    fun registerDatePicker(fragmentManager: FragmentManager) {
        textView.setOnClickListener {
            if (!datePicker.isAdded)
                datePicker.show(fragmentManager, DATE_PICKER_TAG)
        }
    }

    private companion object {
        private const val DATE_PICKER_TAG = "DATE_PICKER"
        private const val DATE_PATTERN = "dd.MM.YYYY"
    }
}