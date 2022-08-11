package io.fasthome.component.di

import io.fasthome.fenestram_messenger.di.viewModel
import org.koin.dsl.module
import io.fasthome.component.permission.PermissionViewModel
import io.fasthome.component.pick_file.PickFileViewModel
import io.fasthome.component.pick_file.PickImageOperations
import io.fasthome.component.pick_file.PickImageOperationsImpl
import io.fasthome.component.pick_file.PickFileOperations
import io.fasthome.component.pick_file.PickFileOperationsImpl
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory

object ComponentModule {
    operator fun invoke() = module {
        viewModel(::PermissionViewModel)
        viewModel(::PickFileViewModel)

        factory(::PickImageOperationsImpl) bindSafe PickImageOperations::class
        factory(::PickFileOperationsImpl) bindSafe PickFileOperations::class
    }
}