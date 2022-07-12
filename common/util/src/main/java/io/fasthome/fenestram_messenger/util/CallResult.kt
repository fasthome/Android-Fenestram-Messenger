package io.fasthome.fenestram_messenger.util

import kotlinx.coroutines.CancellationException

/**
 * Предполагается использовать как результат выполнения бизнес логики в UseCase или Interactor.
 * Ошибка обрабатывается в ViewModel.
 *
 * Метод, который возвращает [CallResult], НЕ должен кидать исключения (кроме [CancellationException]).
 * Все ошибки должны передаваться как [CallResult.Error].
 *
 * @see callForResult
 */
sealed class CallResult<out T> {

    data class Success<out T>(val data: T) : CallResult<T>()

    data class Error(val error: Throwable) : CallResult<Nothing>()
}

inline fun <T> CallResult<T>.onSuccess(action: (T) -> Unit): CallResult<T> = apply {
    if (this is CallResult.Success) {
        action(data)
    }
}

fun <T> CallResult<T>.getOrThrow(): T =
    when (this) {
        is CallResult.Success -> data
        is CallResult.Error -> throw error
    }

fun <T> CallResult<T>.getOrNull(): T? =
    when (this) {
        is CallResult.Success -> data
        is CallResult.Error -> null
    }

fun CallResult<*>.exceptionOrNull(): Throwable? =
    when (this) {
        is CallResult.Success -> null
        is CallResult.Error -> error
    }

fun <T> CallResult<T>.getOrDefault(default: T): T =
    when (this) {
        is CallResult.Success -> data
        is CallResult.Error -> default
    }

inline fun <T, R> CallResult<T>.map(mapper: (T) -> R): CallResult<R> =
    when (this) {
        is CallResult.Error -> this
        is CallResult.Success -> CallResult.Success(mapper(data))
    }

inline fun <T> callForResult(block: () -> T): CallResult<T> =
    try {
        CallResult.Success(block())
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (error: Throwable) {
        CallResult.Error(error)
    }