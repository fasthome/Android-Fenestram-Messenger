package io.fasthome.fenestram_messenger.auth_ad_impl.presentation.login

data class LoginState(
    val loginError: Boolean,
    val passwordError: Boolean,
    val loginButtonAlpha: Float
)