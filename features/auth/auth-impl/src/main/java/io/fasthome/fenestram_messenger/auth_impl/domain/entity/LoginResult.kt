/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.domain.entity

import io.fasthome.fenestram_messenger.auth_api.UserDetail
import io.fasthome.network.tokens.AccessToken
import io.fasthome.network.tokens.RefreshToken

data class LoginResult(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
    val userDetail: UserDetail
)