/**
 * Created by Dmitry Popov on 11.08.2022.
 */
package io.fasthome.fenestram_messenger.util.android

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns


fun Uri.getFileName(context: Context): String? = runCatching {
    context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        cursor.moveToFirst()
        return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME).let(cursor::getString)
    }
}.getOrNull()