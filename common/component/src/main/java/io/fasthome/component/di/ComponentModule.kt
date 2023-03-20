package io.fasthome.component.di

import io.fasthome.component.camera.CameraComponentViewModel
import io.fasthome.component.camera.CameraImageOperations
import io.fasthome.component.camera.CameraImageOperationsImpl
import io.fasthome.component.gallery.GalleryRepository
import io.fasthome.component.gallery.GalleryRepositoryImpl
import io.fasthome.component.permission.PermissionViewModel
import io.fasthome.component.personality_data.PersonalityComponentViewModel
import io.fasthome.component.pick_file.*
import io.fasthome.component.theme.ThemeStorage
import io.fasthome.fenestram_messenger.data.StorageQualifier
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

object ComponentModule {
    operator fun invoke() = module {
        viewModel(::PermissionViewModel)
        viewModel(::PickFileViewModel)
        viewModel(::PersonalityComponentViewModel)
        viewModel(::CameraComponentViewModel)

        factory(::GalleryRepositoryImpl) bindSafe GalleryRepository::class
        factory(::CameraImageOperationsImpl) bindSafe CameraImageOperations::class
        factory(::PickImageOperationsImpl) bindSafe PickImageOperations::class
        factory(::PickFileOperationsImpl) bindSafe PickFileOperations::class
        factory(::ProfileImageUtilImpl) bindSafe ProfileImageUtil::class

        single { ThemeStorage(get(named(StorageQualifier.Simple))) }
    }
}