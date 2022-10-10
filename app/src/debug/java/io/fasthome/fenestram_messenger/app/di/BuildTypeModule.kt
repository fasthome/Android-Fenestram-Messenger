package io.fasthome.fenestram_messenger.app.di

import io.fasthome.fenestram_messenger.core.debug.DebugRepo
import io.fasthome.fenestram_messenger.data.StorageQualifier
import io.fasthome.fenestram_messenger.debug_impl.data.DebugRepoImpl
import io.fasthome.fenestram_messenger.di.bindSafe
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

object BuildTypeModule {

    operator fun invoke(): List<Module> = listOf(
        createAppDebug(),
    )

    private fun createAppDebug() = module {
        single { DebugRepoImpl(storageFactory = get(named(StorageQualifier.Simple))) } bindSafe DebugRepo::class
    }
}