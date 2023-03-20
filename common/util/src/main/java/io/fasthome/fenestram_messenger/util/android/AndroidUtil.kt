/**
 * Created by Dmitry Popov on 11.08.2022.
 */
package io.fasthome.fenestram_messenger.util.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.provider.OpenableColumns


fun Uri.getFileName(context: Context): String? = runCatching {
    context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        cursor.moveToFirst()
        return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME).let(cursor::getString)
    }
}.getOrNull()

@SuppressLint("DiscouragedApi", "InternalInsetResource")
fun getNavigationBarHeight(resources : Resources) : Int{
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    if (resourceId > 0) {
        return resources.getDimensionPixelSize(resourceId);
    }
    return 0;
}