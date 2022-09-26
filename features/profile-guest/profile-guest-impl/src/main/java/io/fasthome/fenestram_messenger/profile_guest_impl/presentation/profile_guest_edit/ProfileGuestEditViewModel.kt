package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_edit

import androidx.lifecycle.viewModelScope
import io.fasthome.component.pick_file.PickFileInterface
import io.fasthome.component.pick_file.ProfileImageUtil
import io.fasthome.fenestram_messenger.data.ProfileImageUrlConverter
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.Message
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.logic.ProfileGuestInteractor
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileGuestEditViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ProfileGuestEditNavigationContract.Params,
    private val profileGuestInteractor: ProfileGuestInteractor,
    private val profileImageUrlConverter: ProfileImageUrlConverter,
    private val pickFileInterface: PickFileInterface,
    private val profileImageUtil: ProfileImageUtil,
) : BaseViewModel<ProfileGuestEditState, ProfileGuestEditEvent>(router, requestParams) {

    init {
        pickFileInterface.resultEvents()
            .onEach {
                when (it) {
                    PickFileInterface.ResultEvent.PickCancelled -> Unit
                    is PickFileInterface.ResultEvent.Picked -> {
                        val bitmap = profileImageUtil.getPhoto(it.tempFile)
                        if (bitmap != null) {
                            updateState { state ->
                                state.copy(
                                    avatarBitmap = bitmap,
                                    chatImageFile = it.tempFile
                                )
                            }
                        } else {
                            showMessage(Message.PopUp(PrintableText.StringResource(R.string.common_unable_to_download)))
                            updateState { state ->
                                state.copy(
                                    avatarBitmap = null,
                                    chatImageFile = null
                                )
                            }
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun createInitialState() =
        ProfileGuestEditState(
            params.userName,
            params.userAvatar,
            null,
            null,
            params.participantsCount
        )

    fun onEditCompletedClick(newName: String) {
        viewModelScope.launch {
            if (newName != params.userName)
                profileGuestInteractor.patchChatName(params.id, newName)

            val imageUrl: String? = currentViewState.chatImageFile?.let { file ->
                profileGuestInteractor.uploadChatAvatar(
                    file.readBytes()
                ).getOrNull()?.imagePath
            }?.apply { substring(20, length) }

            if (imageUrl != null && profileGuestInteractor.patchChatAvatar(params.id, imageUrl)
                    .successOrSendError() != null
            )
                updateState { state ->
                    state.copy(avatarUrl = profileImageUrlConverter.convert(imageUrl))
                }

            exitWithResult(
                ProfileGuestEditNavigationContract.createResult(
                    ProfileGuestEditNavigationContract.Result.ChatEdited(
                        newName,
                        currentViewState.avatarUrl
                    )
                )
            )
        }
    }

    fun onAvatarClicked() {
        pickFileInterface.pickFile()
    }
}