package io.fasthome.fenestram_messenger.uikit.custom_view

import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.Date

class HooliDatePicker(val textView: TextView) {

    private val now = ZonedDateTime.now().toInstant().toEpochMilli()

    private val startDate = ZonedDateTime.parse(START_DATE).toInstant().toEpochMilli()

    private val year = 60L * 60L * 24L * 365L * 1000L

    private val minus100years = now - year * 100L

    private val minus30years = now - year * 30L

    private val constraintsBuilder =
        CalendarConstraints.Builder()
            .setStart(startDate)
            .setEnd(now)
            .setValidator(DateValidatorPointBackward.now())
            .setOpenAt(minus30years)
            .build()
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
        private const val START_DATE = "1900-01-01T00:00:00Z"
        private const val DATE_PICKER_TAG = "DATE_PICKER"
        private const val DATE_PATTERN = "dd.MM.YYYY"
    }
}