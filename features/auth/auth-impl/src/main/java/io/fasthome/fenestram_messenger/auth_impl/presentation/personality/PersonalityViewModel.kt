package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import io.fasthome.component.pick_file.PickFileInterface
import io.fasthome.component.pick_file.ProfileImageUtil
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.Message
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_api.ProfileFeature
import io.fasthome.fenestram_messenger.profile_api.entity.PersonalData
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine


class PersonalityViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val profileFeature: ProfileFeature,
    private val pickFileInterface: PickFileInterface,
    private val params: PersonalityNavigationContract.Params,
    private val profileImageUtil: ProfileImageUtil,
    private val environment: Environment
) : BaseViewModel<PersonalityState, PersonalityEvent>(router, requestParams) {

    private var avatarUrl: String? = null

    init {
        pickFileInterface.resultEvents()
            .onEach {
                when (it) {
                    PickFileInterface.ResultEvent.PickCancelled -> Unit
                    is PickFileInterface.ResultEvent.Picked -> {
                        val bitmap = profileImageUtil.getPhoto(it.tempFile)
                        if (bitmap != null)
                            updateState { state ->
                                state.copy(
                                    avatarBitmap = bitmap,
                                    originalProfileImageFile = it.tempFile,
                                    profileImageUrl = null
                                )
                            }
                        else {
                            showMessage(Message.PopUp(PrintableText.StringResource(R.string.auth_error_photo)))
                            updateState { state ->
                                state.copy(
                                    profileImageUrl = avatarUrl,
                                    avatarBitmap = null,
                                    originalProfileImageFile = null
                                )
                            }
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun createInitialState(): PersonalityState {
        val detail = params.userDetail

        avatarUrl = if (!detail.profileImageUrl.isNullOrEmpty())
            environment.endpoints.apiBaseUrl.dropLast(1) + detail.profileImageUrl
        else
            null

        return PersonalityState(
            listOf(
                PersonalityState.Field(
                    key = PersonalityFragment.EditTextKey.BirthdateKey,
                    text = PrintableText.Raw(detail.birth),
                    visibility = detail.birth.isNotEmpty()
                ),
                PersonalityState.Field(
                    key = PersonalityFragment.EditTextKey.UserNameKey,
                    text = PrintableText.Raw(detail.nickname),
                    visibility = detail.nickname.isNotEmpty()
                ),
                PersonalityState.Field(
                    key = PersonalityFragment.EditTextKey.NameKey,
                    text = PrintableText.Raw(detail.name),
                    visibility = detail.name.isNotEmpty()
                ),
                PersonalityState.Field(
                    key = PersonalityFragment.EditTextKey.MailKey,
                    text = PrintableText.Raw(detail.email),
                    visibility = detail.email.isNotEmpty()
                )
            ),
            avatarBitmap = null,
            originalProfileImageFile = null,
            profileImageUrl = avatarUrl
        )
    }

    fun checkPersonalData(name: String, nickname: String, birthday: String, mail: String) {
        viewModelScope.launch {
            val avatarFile = currentViewState.originalProfileImageFile

            val avatar = when {
                avatarFile != null -> {
                    profileFeature.uploadProfileImage(avatarFile.readBytes())
                        .getOrNull()?.profileImagePath.let {
                            avatarUrl = environment.endpoints.apiBaseUrl.dropLast(1) + it
                            it
                        }
                }
                !avatarUrl.isNullOrEmpty() -> {
                    avatarUrl!!.substring(20, avatarUrl!!.length)
                }
                else -> {
                    null
                }
            }

            val playerId = getFirebaseToken()

            val personalData = PersonalData(
                username = name,
                nickname = nickname,
                birth = birthday,
                email = mail,
                avatar = avatar,
                playerId = playerId
            )

            profileFeature.sendPersonalData(
                personalData.copy(avatar = avatar)
            ).withErrorHandled(
                showErrorType = ShowErrorType.Dialog
            ) {
                exitWithResult(PersonalityNavigationContract.createResult(AuthFeature.AuthResult.Success))
            }
        }
    }

    fun skipPersonalData() {
        exitWithResult(PersonalityNavigationContract.createResult(AuthFeature.AuthResult.Success))
    }

    fun fillData(
        data: String = "",
        hasFocus: Boolean = false,
        key: PersonalityFragment.EditTextKey
    ) {
        val visibleCondition = when (key) {
            PersonalityFragment.EditTextKey.NameKey -> !hasFocus && data.isNotEmpty()
            PersonalityFragment.EditTextKey.UserNameKey -> !hasFocus && data.isNotEmpty()
            PersonalityFragment.EditTextKey.BirthdateKey -> data.isNotEmpty()
            PersonalityFragment.EditTextKey.MailKey -> !hasFocus && Patterns.EMAIL_ADDRESS.matcher(
                data
            ).matches()
        }
        updateState { state ->
            state.copy(
                fieldsData = listOf(
                    PersonalityState.Field(
                        key = key,
                        text = PrintableText.Raw(data),
                        visibility = visibleCondition
                    )
                )
            )
        }
    }

    fun onSelectPhotoClicked() {
        pickFileInterface.pickFile()
    }

    private suspend fun getFirebaseToken() = suspendCoroutine<String> { continuation ->
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                val token: String = task.result
                continuation.resumeWith(Result.success(token))
            }.addOnFailureListener {
                continuation.resumeWith(Result.failure(it))
            }
    }

}