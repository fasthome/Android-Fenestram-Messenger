package io.fasthome.fenestram_messenger.util

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

fun Fragment.resultLauncher(onResult: (ActivityResult) -> Unit) =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onResult(result)
        }
    }

fun getGalleryIntent(): Intent{
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/*"
    return intent
}