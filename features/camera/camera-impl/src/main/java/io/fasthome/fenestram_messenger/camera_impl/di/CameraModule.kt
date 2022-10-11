/**
 * Created by Dmitry Popov on 10.10.2022.
 */
package io.fasthome.fenestram_messenger.camera_impl.di

import io.fasthome.fenestram_messenger.camera_api.CameraFeature
import io.fasthome.fenestram_messenger.camera_impl.CameraFeatureImpl
import io.fasthome.fenestram_messenger.camera_impl.presentation.camera.CameraViewModel
import io.fasthome.fenestram_messenger.camera_impl.presentation.confirm.ConfirmPhotoViewModel
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.factory
import io.fasthome.fenestram_messenger.di.viewModel
import org.koin.dsl.module

object CameraModule {
    operator fun invoke() = listOf(
        createFeatureModule(),
        createDataModule(),
        createDomainModule(),
        createPresentationModule(),
    )

    private fun createFeatureModule() = module {
        factory(::CameraFeatureImpl) bindSafe CameraFeature::class
    }

    private fun createDataModule() = module {
    }

    private fun createDomainModule() = module {
    }

    private fun createPresentationModule() = module {
        viewModel(::CameraViewModel)
        viewModel(::ConfirmPhotoViewModel)
    }
}