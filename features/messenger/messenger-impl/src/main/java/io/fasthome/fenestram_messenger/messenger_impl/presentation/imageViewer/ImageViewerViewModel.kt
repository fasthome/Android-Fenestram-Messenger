package io.fasthome.fenestram_messenger.messenger_impl.presentation.imageViewer

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class ImageViewerViewModel(
    router : ContractRouter,
    requestParams : RequestParams,
    private val params : ImageViewerContract.Params
) : BaseViewModel<ImageViewerState, ImageViewerEvent>(
    router, requestParams
) {
    override fun createInitialState(): ImageViewerState = ImageViewerState(params.imageUrl)

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }
}