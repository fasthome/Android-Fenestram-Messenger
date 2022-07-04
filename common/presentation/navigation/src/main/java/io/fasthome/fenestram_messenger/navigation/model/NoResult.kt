package io.fasthome.fenestram_messenger.navigation.model

import android.os.Parcelable

abstract class NoResult private constructor() : Parcelable {
    init {
        throw IllegalStateException()
    }
}
