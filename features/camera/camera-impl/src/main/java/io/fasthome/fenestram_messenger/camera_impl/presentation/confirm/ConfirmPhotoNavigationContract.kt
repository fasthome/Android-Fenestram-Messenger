package io.fasthome.fenestram_messenger.camera_impl.presentation.confirm

import io.fasthome.fenestram_messenger.camera_api.ConfirmParams
import io.fasthome.fenestram_messenger.camera_api.ConfirmResult
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract

object ConfirmPhotoNavigationContract : NavigationContract<ConfirmParams, ConfirmResult>(ConfirmPhotoFragment::class)