package io.fasthome.fenestram_messenger.util

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import io.fasthome.fenestram_messenger.util.kotlin.switchJob

/**
 * [Lifecycle.whenStateAtLeast] pauses, but does NOT cancel execution on,
 * for example, [Lifecycle.Event.ON_STOP] for [state] == [Lifecycle.State.STARTED].
 * But we need to cancel launched coroutine.
 */
fun LifecycleOwner.whenAtLeastWithCancel(
    state: Lifecycle.State,
    block: CoroutineScope.() -> Unit,
) {
    lifecycle.addObserver(object : LifecycleEventObserver {
        private var job by switchJob()

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.upTo(state) -> job = lifecycleScope.launch { block(this) }
                Lifecycle.Event.downFrom(state) -> job?.cancel()
                else -> Unit
            }
        }
    })
}

private class FlowWhenStartedObserver<T>(
    lifecycleOwner: LifecycleOwner,
    private val flow: Flow<T>,
    private val collector: suspend (T) -> Unit,
    private val collectLatest: Boolean = false,
) {

    private var job: Job? = null

    init {
        lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { source: LifecycleOwner, event: Lifecycle.Event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    job = source.lifecycleScope.launch {
                        if (collectLatest) {
                            flow.collectLatest(collector)
                        } else {
                            flow.collect {
                                collector.invoke(it)
                            }
                        }
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    job?.cancel()
                    job = null
                }
                else -> Unit
            }
        })
    }
}

// todo: replace with "addRepeatingJob" from lifecycle:lifecycle-runtime-ktx:2.4.x
fun <T> Flow<T>.collectWhenStarted(
    lifecycleOwner: LifecycleOwner,
    collector: suspend (T) -> Unit,
) {
    FlowWhenStartedObserver(lifecycleOwner, this, collector)
}

fun <T> Flow<T>.collectLatestWhenStarted(
    lifecycleOwner: LifecycleOwner,
    collector: suspend (T) -> Unit,
) {
    FlowWhenStartedObserver(lifecycleOwner, this, collector, true)
}

fun <T> Flow<T>.launchWhenStarted(lifecycleOwner: LifecycleOwner) {
    FlowWhenStartedObserver(lifecycleOwner, this, {})
}

fun <T> MutableSharedFlow<T>.launchWhenSubscribed(
    scope: CoroutineScope,
    block: suspend (Job) -> Unit,
) {
    var job: Job? = null

    subscriptionCount
        .map { count -> count > 0 }
        .distinctUntilChanged()
        .onEach { active ->
            job = if (active) {
                Job().also { job ->
                    scope.launch(job) { block(job) }
                }
            } else {
                job?.cancel()
                null
            }
        }
        .onCompletion { job?.cancel() }
        .launchIn(scope)
}

fun Lifecycle.doOnStartStop(
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    addObserver(object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            onStart()
        }

        override fun onStop(owner: LifecycleOwner) {
            onStop()
        }
    })
}

fun Lifecycle.doOnResumePause(
    onResume: () -> Unit,
    onPause: () -> Unit,
) {
    addObserver(object : DefaultLifecycleObserver {

        override fun onResume(owner: LifecycleOwner) {
            onResume()
        }

        override fun onPause(owner: LifecycleOwner) {
            onPause()
        }
    })
}

fun Lifecycle.doOnDestroy(action: () -> Unit) {
    addObserver(object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            action()
        }
    })
}

fun Lifecycle.doOnCreate(action: () -> Unit) {
    addObserver(object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            action()
        }
    })
}