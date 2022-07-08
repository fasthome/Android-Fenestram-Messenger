package io.fasthome.component.di

import io.fasthome.fenestram_messenger.di.viewModel
import org.koin.dsl.module
import io.fasthome.component.permission.PermissionViewModel

object ComponentModule {
    operator fun invoke() = module {
        viewModel(::PermissionViewModel)
    }
}