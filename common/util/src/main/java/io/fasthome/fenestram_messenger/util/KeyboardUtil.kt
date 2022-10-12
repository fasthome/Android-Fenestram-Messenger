package io.fasthome.fenestram_messenger.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun View.showKeyboard() {
    requestFocus()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        windowInsetsController?.show(WindowInsets.Type.ime())
    } else {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(findFocus(), InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Activity.hideKeyboard(clearFocus: Boolean = false) {
    val view = findViewById<View>(android.R.id.content) ?: return
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).run {
        hideSoftInputFromWindow(view.windowToken, 0)
        hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }
    if (clearFocus) view.clearFocus()
}

fun EditText.lastCharFocus() = setSelection(text.length)