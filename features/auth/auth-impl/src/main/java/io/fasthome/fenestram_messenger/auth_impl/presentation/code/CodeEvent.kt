package io.fasthome.fenestram_messenger.auth_impl.presentation.code

sealed class CodeEvent {

    class ChangeTime(val time: String?) : CodeEvent()

    class ValidateCode(val isValid : Boolean) : CodeEvent()

}