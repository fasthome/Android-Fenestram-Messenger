package io.fasthome.fenestram_messenger.camera_impl.presentation.confirm

import io.fasthome.fenestram_messenger.camera_api.ConfirmParams
import io.fasthome.fenestram_messenger.camera_api.ConfirmResult
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content

class ConfirmPhotoViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
    private val params: ConfirmParams,
) : BaseViewModel<ConfirmPhotoState, ConfirmPhotoEvent>(router, requestParams) {

    override fun createInitialState() = ConfirmPhotoState(
        content = params.content,
    )

    fun onSaveClicked() {
        val file = when (val content = params.content) {
            is Content.FileContent -> content.file
            is Content.LoadableContent -> null
        }

        file?.let { exitWithResult(ConfirmResult.Action.Confirm(it)) }
    }

    fun onRetakeClicked() {
        exitWithResult(ConfirmResult.Action.Retake)
    }

    override fun onBackPressed(): Boolean {
        exitWithResult(ConfirmResult.Action.Cancel)
        return true
    }

    private fun exitWithResult(action: ConfirmResult.Action) {
        val result = ConfirmResult(action)
        exitWithResult(ConfirmPhotoNavigationContract.createResult(result))
    }

}