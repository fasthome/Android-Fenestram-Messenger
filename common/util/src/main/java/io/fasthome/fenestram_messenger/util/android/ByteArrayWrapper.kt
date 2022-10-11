package io.fasthome.fenestram_messenger.util.android

class ByteArrayWrapper(
    val array: ByteArray,
) {
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is ByteArrayWrapper -> false
        array === other.array -> true
        else -> array contentEquals other.array
    }

    override fun hashCode(): Int = array.contentHashCode()
}

fun ByteArray.wrap(): ByteArrayWrapper {
    return ByteArrayWrapper(this)
}