package io.fasthome.fenestram_messenger.util.kotlin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*

/**
 * Позаимствовано из https://github.com/aartikov/Sesame/tree/master/sesame-activable
 */
interface Activable {

    val activeFlow: StateFlow<Boolean>

    fun onActive()

    fun onInactive()
}

val Activable.active get() = activeFlow.value

fun Activable(): Activable = ActivableImpl()

internal class ActivableImpl : Activable {

    override val activeFlow = MutableStateFlow(false)

    override fun onActive() {
        activeFlow.value = true
    }

    override fun onInactive() {
        activeFlow.value = false
    }
}

/**
 * Преобразование [Flow] в "активируемый" flow.
 * "Акивируемый" flow заново подписывается на [originalFlow] КАЖДЫЙ раз, когда [activable] становится активным,
 * и отписывается, когда [activable] становится неактивным.
 */
fun <T> activableFlow(
    originalFlow: Flow<T>,
    activable: Activable,
    scope: CoroutineScope,
): Flow<T> {
    val flow = MutableSharedFlow<T>()

    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
        var job: Job? by switchJob()

    val hasSubscriptions = flow.subscriptionCount
        .map { it > 0 }
        .distinctUntilChanged()

    combine(activable.activeFlow, hasSubscriptions, Boolean::and)
        .onEach { active ->
            job = if (active) {
                originalFlow
                    .onEach { flow.emit(it) }
                    .launchIn(scope)
            } else {
                null
            }
        }
        .launchIn(scope)

    return flow
}