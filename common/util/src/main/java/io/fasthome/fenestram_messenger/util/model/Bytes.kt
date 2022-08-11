package io.fasthome.fenestram_messenger.util.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Bytes(val bytes: Long) : Parcelable {

    companion object {
        const val BYTES_PER_MB = 1024L * 1024
    }

    fun megabytes(): Long = bytes / BYTES_PER_MB

    operator fun compareTo(other: Bytes): Int = bytes.compareTo(other.bytes)
}