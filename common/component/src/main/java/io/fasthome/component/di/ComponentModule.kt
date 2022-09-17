package io.fasthome.component.di

import io.fasthome.fenestram_messenger.di.viewModel
import org.koin.dsl.module
import io.fasthome.component.permission.PermissionViewModel
import io.fasthome.component.pick_file.*
import io.fasthome.component.personality_data.*
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory

object ComponentModule {
    operator fun invoke() = module {
        viewModel(::PermissionViewModel)
        viewModel(::PickFileViewModel)
        viewModel(::PersonalityComponentViewModel)

        factory(::PickImageOperationsImpl) bindSafe PickImageOperations::class
        factory(::PickFileOperationsImpl) bindSafe PickFileOperations::class

        factory(::ProfileImageUtilImpl) bindSafe ProfileImageUtil::class
    }
}