/**
 * Created by Dmitry Popov on 22.05.2022.
 */
package io.fasthome.fenestram_messenger.util.kotlin

import kotlinx.coroutines.Job
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


fun <T> switchObject(cancelPrev: (T) -> Unit) = object : ReadWriteProperty<Any?, T?> {
    private var t: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? = t

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        t?.apply(cancelPrev)
        t = value
    }
}

fun switchJob() = switchObject(cancelPrev = Job::cancel)

inline fun <T> ifOrNull(condition: Boolean, valueProvider: () -> T): T? =
    if (condition) valueProvider() else null
