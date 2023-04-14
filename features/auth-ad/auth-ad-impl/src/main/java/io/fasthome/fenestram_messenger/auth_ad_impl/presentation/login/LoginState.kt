package io.fasthome.fenestram_messenger.auth_ad_impl.presentation.login

import io.fasthome.fenestram_messenger.util.PrintableText

data class LoginState(
    val loginErrorMessage: PrintableText,
    val passwordErrorMessage: PrintableText,
    val loginButtonEnabled: Boolean,
)