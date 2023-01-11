package io.fasthome.fenestram_messenger.app.di

import io.fasthome.fenestram_messenger.core.debug.DebugRepo
import io.fasthome.fenestram_messenger.core.debug.NormalDebugRepo
import org.koin.core.module.Module
import org.koin.dsl.module
import io.fasthome.fenestram_messenger.di.bindSafe

object BuildTypeModule {

    operator fun invoke(): List<Module> = listOf(
        createAppRelease(),
    )

    private fun createAppRelease() = module {
        single { NormalDebugRepo() } bindSafe DebugRepo::class
    }
}