package io.fasthome.fenestram_messenger.auth_impl.presentation.code

sealed class CodeState {
    data class GlobalState(val filled: Boolean, val error: Boolean, val autoFilling: String?) :
        CodeState()

    class ChangeTime(val time: String?) : CodeState()
}
