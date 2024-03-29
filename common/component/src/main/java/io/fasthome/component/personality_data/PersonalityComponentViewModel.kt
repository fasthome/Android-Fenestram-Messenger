/**
 * Created by Dmitry Popov on 17.09.2022.
 */
package io.fasthome.component.personality_data

import androidx.lifecycle.viewModelScope
import io.fasthome.component.R
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_api.UserDetail
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.REGEX_EMAIL_PATTERN
import io.fasthome.fenestram_messenger.util.getPrintableRawText
import io.fasthome.fenestram_messenger.util.kotlin.ifOrNull
import io.fasthome.fenestram_messenger.util.kotlin.switchJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PersonalityComponentViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: PersonalityParams,
    private val authFeature: AuthFeature
) : BaseViewModel<PersonalityState, PersonalityEvent>(router, requestParams), PersonalityInterface {

    private var validateJob by switchJob()

    private var userDetail = params.userDetail

    private var users: List<AuthFeature.User>? = null

    init {
        viewModelScope.launch {
            users = authFeature.getUsers().successOrSendError()
        }
    }

    override val fieldStateChanges: Flow<FillState> = viewState
        .map {
            when {
                it.fields.any { field ->
                    field.value.error != null
                } -> FillState.Error
                it.fields.all { field ->
                    field.value.error == null
                } && getPrintableRawText(it.fields[EditTextKey.UsernameKey]?.validateText).isNotEmpty() -> FillState.Filled
                else -> FillState.Empty
            }
        }

    override fun getFields(): UserDetail {
        val fields = currentViewState.fields
        return UserDetail(
            id = 0,
            phone = "",
            profileImageUrl = null,
            name = fields[EditTextKey.UsernameKey]!!.validateText?.let { getPrintableRawText(it) }
                ?: "",
            email = fields[EditTextKey.MailKey]!!.validateText?.let { getPrintableRawText(it) }
                ?: "",
            birth = fields[EditTextKey.BirthdateKey]!!.validateText?.let { getPrintableRawText(it) }
                ?: "",
            nickname = fields[EditTextKey.NicknameKey]!!.validateText?.let { getPrintableRawText(it) }
                ?: "",
        )
    }

    override fun setFields(userDetail: UserDetail) {
        this.userDetail = userDetail
        updateState { state ->
            getInitialState(false)
        }
        createInitialState()
    }

    override fun invalidateState() {
        updateState {
            createInitialState()
        }
    }

    override fun createInitialState(): PersonalityState {
        sendEvent(PersonalityEvent.VisibleName(params.nameVisible))
        return getInitialState(true)
    }

    private fun getInitialState(needValidate: Boolean): PersonalityState {
        return PersonalityState(
            fields = mapOf(
                EditTextKey.UsernameKey to Field(
                    key = EditTextKey.UsernameKey,
                    text = PrintableText.Raw(userDetail?.name ?: ""),
                    visibilityIcon = userDetail?.name?.isNotEmpty() ?: false,
                    error = null,
                    validateText = null
                ),
                EditTextKey.MailKey to Field(
                    key = EditTextKey.MailKey,
                    text = PrintableText.Raw(userDetail?.email ?: ""),
                    visibilityIcon = userDetail?.email?.isNotEmpty() ?: false,
                    error = null,
                    validateText = null
                ),
                EditTextKey.NicknameKey to Field(
                    key = EditTextKey.NicknameKey,
                    text = PrintableText.Raw(userDetail?.nickname ?: ""),
                    visibilityIcon = userDetail?.nickname?.isNotEmpty() ?: false,
                    error = null,
                    validateText = null
                ),
                EditTextKey.BirthdateKey to Field(
                    key = EditTextKey.BirthdateKey,
                    text = PrintableText.Raw(userDetail?.birth ?: ""),
                    visibilityIcon = userDetail?.birth?.isNotEmpty() ?: false,
                    error = null,
                    validateText = null
                )
            ),
            singleValidate = SingleValidate(needValidate, needValidate = false),
            visibilityIcon = params.visibilityIcons
        )
    }

    fun onFieldChanged(
        editTextKey: EditTextKey,
        inputText: String,
        singleValidate: Boolean = false,
        needValidate: Boolean = true
    ) {
        updateState { state ->
            val fields = state.fields.toMutableMap()
            fields[editTextKey] = fields[editTextKey]!!.copy(
                error = null,
                validateText = null
            )
            state.copy(
                fields = fields
            )
        }
        if (!validateField(editTextKey, inputText.trim(), singleValidate, needValidate)) return

    }


    /**
     * @return true, если валидация прошла успешно.
     */
    private fun validateField(
        editTextKey: EditTextKey,
        inputText: String,
        singleValidate: Boolean,
        needValidate: Boolean
    ): Boolean {
        val isValid: Boolean
        val errorPrintableText: PrintableText
        when (editTextKey) {
            EditTextKey.UsernameKey -> {
                when {
                    inputText.startsWith(" ") || inputText.endsWith(" ") -> {
                        isValid = false
                        errorPrintableText = PrintableText.StringResource(
                            R.string.personality_incorrect_name
                        )
                    }
                    inputText.length in 2..15 -> {
                        isValid = true
                        userDetail = userDetail?.copy(
                            name = inputText
                        )
                        errorPrintableText = PrintableText.StringResource(
                            R.string.personality_incorrect_name
                        )
                    }
                    inputText.length == 1 -> {
                        isValid = false
                        errorPrintableText = PrintableText.StringResource(
                            R.string.personality_incorrect_length_name
                        )
                    }
                    else -> {
                        isValid = false
                        errorPrintableText = PrintableText.StringResource(
                            R.string.personality_empty_name_error
                        )
                    }
                }
            }
            EditTextKey.NicknameKey -> {
                when {
                    inputText.isEmpty() -> {
                        isValid = false
                        errorPrintableText = PrintableText.StringResource(
                            R.string.personality_empty_nickname_error
                        )
                    }
                    users?.any { user -> user.nickname?.let { it == inputText } ?: false }
                        ?: false -> {
                        isValid = false
                        errorPrintableText = PrintableText.StringResource(
                            R.string.personality_same_nickname
                        )
                    }
                    inputText.length in PersonalityComponentFragment.MIN_SYMBOLS_NICKNAME..PersonalityComponentFragment.MAX_SYMBOLS_NICKNAME -> {
                        isValid = true
                        userDetail = userDetail?.copy(
                            nickname = inputText
                        )
                        errorPrintableText = PrintableText.StringResource(
                            R.string.personality_incorrect_length_nickname
                        )
                    }
                    inputText.isNotEmpty() -> {
                        isValid = false
                        errorPrintableText = PrintableText.StringResource(
                            R.string.personality_incorrect_length_nickname
                        )
                    }
                    else -> {
                        isValid = false
                        errorPrintableText = PrintableText.StringResource(
                            R.string.personality_incorrect_nickname
                        )
                    }
                }
            }
            EditTextKey.BirthdateKey -> {
                isValid = inputText.isNotEmpty()
                if (isValid) {
                    userDetail = userDetail?.copy(
                        birth = inputText
                    )
                }
                errorPrintableText = PrintableText.StringResource(
                    R.string.personality_incorrect_birthday
                )
            }
            EditTextKey.MailKey -> {
                isValid = inputText.matches(REGEX_EMAIL_PATTERN)
                if (isValid) {
                    userDetail = userDetail?.copy(
                        email = inputText
                    )
                }
                errorPrintableText = PrintableText.StringResource(
                    R.string.personality_incorrect_mail
                )
            }
        }

        if (singleValidate) {
            viewModelScope.launch {
                jobAction(editTextKey, isValid, errorPrintableText, inputText, needValidate)
            }
        } else {
            validateJob = viewModelScope.launch {
                delay(VALIDATE_DELAY_MILLIS)
                jobAction(editTextKey, isValid, errorPrintableText, inputText, needValidate)
            }
        }

        return isValid
    }

    private fun jobAction(
        editTextKey: EditTextKey,
        isValid: Boolean,
        errorPrintableText: PrintableText,
        inputText: String,
        needValidate: Boolean
    ) {
        updateState { state ->
            val fields = state.fields.toMutableMap()
            fields[editTextKey] = fields[editTextKey]!!.copy(
                error = ifOrNull(
                    !isValid && needValidate && checkInputText(inputText, editTextKey)
                ) {
                    errorPrintableText
                },
                visibilityIcon = isValid && needValidate,
                text = null,
                validateText = PrintableText.Raw(inputText)
            )
            state.copy(
                fields = fields,
                singleValidate = SingleValidate(singleValidate = false, needValidate = false)
            )
        }
    }

    private fun checkInputText(inputText: String, editTextKey: EditTextKey): Boolean = inputText.isNotEmpty() ||
            (editTextKey == EditTextKey.UsernameKey && !userDetail?.name.isNullOrEmpty()) ||
            (editTextKey == EditTextKey.NicknameKey && !userDetail?.nickname.isNullOrEmpty())

    companion object {
        private const val VALIDATE_DELAY_MILLIS = 300L
    }
}