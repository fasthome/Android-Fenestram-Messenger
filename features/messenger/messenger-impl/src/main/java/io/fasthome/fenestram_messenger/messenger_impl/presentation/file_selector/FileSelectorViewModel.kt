/**
 * Created by Dmitry Popov on 02.02.2023.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector

import androidx.lifecycle.viewModelScope
import io.fasthome.component.gallery.GalleryOperations
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch

class FileSelectorViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val galleryOperations: GalleryOperations
) : BaseViewModel<FileSelectorState, FileSelectorEvent>(router, requestParams) {

    init {
        viewModelScope.launch {
            //todo suspend? callforresult?
            val images = galleryOperations.getGalleryImages(0, 30)

            updateState { it.copy(images = images) }
        }
    }

    override fun createInitialState(): FileSelectorState = FileSelectorState(
        images = listOf()
    )

}