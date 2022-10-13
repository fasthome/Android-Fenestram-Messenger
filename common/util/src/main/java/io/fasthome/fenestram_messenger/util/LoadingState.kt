package io.fasthome.fenestram_messenger.util

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding

sealed class LoadingState<out E, out T> {
    object None : LoadingState<Nothing, Nothing>()
    object Loading : LoadingState<Nothing, Nothing>()
    data class Error<out E>(val error: E, val throwable: Throwable) : LoadingState<E, Nothing>()
    data class Success<out T>(val data: T) : LoadingState<Nothing, T>()

    companion object
}

fun LoadingState.Companion.Error() = LoadingState.Error(Unit, Exception())
fun LoadingState.Companion.Success() = LoadingState.Success(Unit)

fun LoadingState.Companion.fromLoading(isLoading: Boolean): LoadingState<Nothing, Unit> =
    if (isLoading) LoadingState.Loading else LoadingState.Success()

val LoadingState<*, *>.isLoading get() = this == LoadingState.Loading
val LoadingState<*, *>.isError get() = this is LoadingState.Error
val LoadingState<*, *>.isSuccess get() = this is LoadingState.Success

val <T> LoadingState<*, T>.dataOrNull get(): T? = (this as? LoadingState.Success)?.data

fun List<LoadingState<*, *>>.commonState(): LoadingState<Unit, Unit> = when {
    any { it is LoadingState.Error } -> LoadingState.Error()
    all { it is LoadingState.Success } -> LoadingState.Success()
    any { it is LoadingState.Loading } -> LoadingState.Loading
    else -> LoadingState.None
}

fun <E> List<LoadingState<E, *>>.commonStateWithError(): LoadingState<E, Unit> {
    val firstErrorState: LoadingState.Error<E>? = filterIsInstance<LoadingState.Error<E>>().firstOrNull()

    return when {
        firstErrorState != null -> LoadingState.Error(firstErrorState.error, Exception())
        all { it is LoadingState.Success } -> LoadingState.Success()
        any { it is LoadingState.Loading } -> LoadingState.Loading
        else -> LoadingState.None
    }
}

inline fun <T> renderLoadingState(
    loadingState: LoadingState<ErrorInfo, T>,
    progressContainer: View?,
    contentContainer: View?,
    renderData: (T) -> Unit = {},
    renderError : (error : ErrorInfo, throwable : Throwable) -> Unit
) {
    Log.d("LoadingState", "renderLoadingState: $loadingState")
    progressContainer?.isVisible = loadingState.isLoading
    contentContainer?.isVisible = loadingState.isSuccess

    if (loadingState is LoadingState.Error) {
        renderError(loadingState.error, loadingState.throwable)
    }

    loadingState.dataOrNull?.apply(renderData)
}