package io.fasthome.fenestram_messenger.core.time

import android.os.SystemClock
import java.time.*

interface TimeProvider {

    val currentTimeMillis: Long

    val elapsedRealtime: Long

    fun nowZoned(): ZonedDateTime = ZonedDateTime.now()

    fun now(): LocalDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(currentTimeMillis),
        ZoneId.systemDefault(),
    )

    fun today(): LocalDate = now().toLocalDate()
}

class RealTimeProvider : TimeProvider {

    override val currentTimeMillis: Long get() = System.currentTimeMillis()

    override val elapsedRealtime: Long get() = SystemClock.elapsedRealtime()
}