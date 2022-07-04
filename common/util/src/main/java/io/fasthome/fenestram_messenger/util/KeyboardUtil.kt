package io.fasthome.fenestram_messenger.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.showKeyboard() {
    if (requestFocus()) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
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