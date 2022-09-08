package io.fasthome.network.tokens

import io.fasthome.fenestram_messenger.core.exceptions.InternetConnectionException
import io.fasthome.fenestram_messenger.core.exceptions.UnauthorizedException
import io.fasthome.fenestram_messenger.core.exceptions.WrongServerResponseException
import io.fasthome.fenestram_messenger.util.*

@JvmInline
value class AccessToken(val s: String)

@JvmInline
value class RefreshToken(val s: String)

interface TokensRepo {
    suspend fun saveTokens(accessToken: AccessToken, refreshToken: RefreshToken)
    suspend fun getAccessToken(): AccessToken
    suspend fun updateToken(): AccessToken
    suspend fun isHaveRefreshToken(): Boolean
    suspend fun clearTokens()
}

class TokensRepoImpl(
    private val refreshTokenStorage: RefreshTokenStorage,
    private val accessTokenStorage: AccessTokenStorage,
    private val inMemoryTokensStorage: InMemoryTokensStorage,
    private val tokensService: TokensService,
) : TokensRepo {

    private val updateTokensAction: NonCancellableAction<AccessToken> =
        NonCancellableAction(action = {
            try {
                val prevRefreshToken = checkNotNull(refreshTokenStorage.getRefreshToken()) { "No refresh token!" }
                val prevAccessToken = checkNotNull(accessTokenStorage.getAccessToken()) { "No access token!" }
                val updateResult = callForResult {
                    tokensService.callUpdateToken(prevAccessToken, prevRefreshToken)
                }
                val response = updateResult.getOrThrow()
                val accessToken = AccessToken(response.accessToken)
                val refreshToken = RefreshToken(response.refreshToken)
                saveTokens(accessToken, refreshToken)
                accessToken
            } catch (exception: Exception) {
                throw when (exception) {
                    is InternetConnectionException -> exception
                    else -> TokenUpdateException(exception)
                }
            }
        })

    override suspend fun saveTokens(accessToken: AccessToken, refreshToken: RefreshToken) {
        inMemoryTokensStorage.saveAccessToken(accessToken)
        accessTokenStorage.setAccessToken(accessToken)
        refreshTokenStorage.setRefreshToken(refreshToken)
    }

    override suspend fun getAccessToken(): AccessToken =
        updateTokensAction.getCurrentActionResult()
            ?: inMemoryTokensStorage.getAccessToken()
            ?: updateToken()

    override suspend fun updateToken(): AccessToken = updateTokensAction.invoke()

    override suspend fun isHaveRefreshToken(): Boolean =
        refreshTokenStorage.getRefreshToken() != null

    override suspend fun clearTokens() {
        inMemoryTokensStorage.clear()
        refreshTokenStorage.clearRefreshToken()
    }
}