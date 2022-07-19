/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import android.Manifest
import android.net.Uri
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch

class ProfileViewModel(
    router : ContractRouter,
    requestParams : RequestParams,
    private val permissionInterface: PermissionInterface,
) : BaseViewModel<ProfileState, ProfileEvent>(router, requestParams) {

    override fun createInitialState(): ProfileState {
        return ProfileState(null, false)
    }

    fun requestPermissionAndLoadPhoto() {
        viewModelScope.launch {
            val permissionGranted = permissionInterface.request(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permissionGranted) {
                sendEvent(ProfileEvent.LaunchGallery)
            }
        }
    }

    fun onUpdatePhoto(data: Uri?) {
        if (data == null)
            return
        updateState { state ->
            state.copy(avatar = data)
        }
    }

    fun editClicked(){
        updateState { state ->
            state.copy(isEdit = true)
        }
    }

    fun cancelClicked(){
        updateState { state ->
            state.copy(isEdit = false)
        }
    }
}