/**
 * Created by Vladimir Rudakov on 24.03.2023.
 */
package io.fasthome.fenestram_messenger.tasks_impl.di

import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.viewModel
import io.fasthome.fenestram_messenger.tasks_api.TasksFeature
import io.fasthome.fenestram_messenger.tasks_api.mapper.TaskMapper
import io.fasthome.fenestram_messenger.tasks_impl.TasksFeatureImpl
import io.fasthome.fenestram_messenger.tasks_impl.data.repo_impl.TasksRepoImpl
import io.fasthome.fenestram_messenger.tasks_impl.data.service.TasksService
import io.fasthome.fenestram_messenger.tasks_impl.data.storage.MockTasksStorage
import io.fasthome.fenestram_messenger.tasks_impl.domain.logic.TasksInteractor
import io.fasthome.fenestram_messenger.tasks_impl.domain.repo.TasksRepo
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.TaskEditorViewModel
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.TasksViewModel
import io.fasthome.network.di.singleAuthorizedService
import org.koin.dsl.module

object TasksModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::TasksFeatureImpl) bindSafe TasksFeature::class
    }

    private fun createDataModule() = module {
        factory(::TasksRepoImpl) bindSafe TasksRepo::class
        singleAuthorizedService(::TasksService)
        single { MockTasksStorage() }
    }

    private fun createDomainModule() = module {
        factory(::TasksInteractor)
    }

    private fun createPresentationModule() = module {
        viewModel(::TasksViewModel)
        viewModel(::TaskEditorViewModel)
        factory(::TaskMapper)
    }
}