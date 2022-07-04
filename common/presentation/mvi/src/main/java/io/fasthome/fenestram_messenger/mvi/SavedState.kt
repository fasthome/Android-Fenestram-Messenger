package io.fasthome.fenestram_messenger.mvi

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle

object SavedState {
    const val KEY_SAVED_STATE = "KEY_SAVED_STATE"
}

fun <S : Parcelable> SavedStateHandle.provideSavedState(
    key: String = SavedState.KEY_SAVED_STATE,
    provider: () -> S,
): S? {
    setSavedStateProvider(key) {
        bundleOf(key to provider())
    }
    val bundle = get<Bundle>(key)
    return bundle?.getParcelable(key)
}