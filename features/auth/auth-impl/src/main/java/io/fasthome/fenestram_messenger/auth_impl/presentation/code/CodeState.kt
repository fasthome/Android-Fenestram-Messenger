package io.fasthome.fenestram_messenger.auth_impl.presentation.code

data class CodeState(val filled: Boolean, val error: Boolean, val autoFilling: String?)