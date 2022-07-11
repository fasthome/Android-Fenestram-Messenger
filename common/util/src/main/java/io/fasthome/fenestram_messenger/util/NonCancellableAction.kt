package io.fasthome.fenestram_messenger.util

import io.fasthome.fenestram_messenger.util.kotlin.getOrSet
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Класс для выполнения неотменяемого действия, которое не должно выполняться параллельно.
 *
 * Лямбда [action] и метод [invoke] не принимают аргументы, т.к. не каждый вызов [invoke] приводит к вызову [action].
 */
class NonCancellableAction<T>(
    private val action: suspend () -> T,
) {

    private val mutex = Mutex()
    private var deferred: Deferred<T>? = null

    suspend fun invoke(): T {
        val deferred = mutex.withLock {
            this::deferred.getOrSet {
                coroutineScope {
                    async(NonCancellable) {
                        try {
                            action()
                        } finally {
                            deferred = null
                        }
                    }
                }
            }
        }
        return deferred.await()
    }

    suspend fun getCurrentActionResult(): T? = deferred?.await()
}