package io.fasthome.fenestram_messenger.auth_ad_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LoginResponse(
    @SerialName("login")
    val isLoginWrong: Boolean,

    @SerialName("password")
    val isPasswordWrong: Boolean
)