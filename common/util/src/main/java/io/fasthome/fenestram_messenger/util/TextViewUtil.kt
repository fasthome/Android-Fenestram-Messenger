package io.fasthome.fenestram_messenger.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.TextView
import android.widget.Toast

fun TextView.copyTextToClipBoard(message: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", text)
    clipboardManager.setPrimaryClip(clipData)
    SingleToast.show(
        context,
        message,
        Toast.LENGTH_SHORT
    )
}
