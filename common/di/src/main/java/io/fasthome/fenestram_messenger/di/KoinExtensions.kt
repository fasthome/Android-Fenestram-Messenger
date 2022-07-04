package io.fasthome.fenestram_messenger.di

import org.koin.core.instance.InstanceFactory
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.bind
import kotlin.reflect.KClass

/**
 * Метод [org.koin.dsl.bind] из Koin не делает проверку типов.
 * Поэтому добавлен такой метод, который проверяет, что [B] является подтипом [T].
 *
 * TODO: заменить на метод из Koin:
 * https://github.com/InsertKoinIO/koin/pull/1227
 */
infix fun <T : B, B : Any> Pair<Module, InstanceFactory<T>>.bindSafe(clazz: KClass<B>): Pair<Module, InstanceFactory<T>> {
    bind(clazz)
    return this
}

inline fun <reified T : B, reified B : Any> Module.bind(qualifier: Qualifier? = null) {
    single<B>(qualifier = qualifier) { parameters -> get<T> { parameters } }
}