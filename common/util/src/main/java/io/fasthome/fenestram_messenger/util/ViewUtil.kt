/**
 * Created by Dmitry Popov on 19.07.2022.
 */
package io.fasthome.fenestram_messenger.util

import android.view.View
import kotlinx.coroutines.suspendCancellableCoroutine

inline fun View.onClick(crossinline action: () -> Unit) {
    setOnClickListener { action() }
}

suspend fun View.awaitPost() = suspendCancellableCoroutine<Unit> { continuation ->
    post { continuation.resume(Unit, null) }
}