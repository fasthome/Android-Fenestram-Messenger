package io.fasthome.network.tokens

class InMemoryTokensStorage {
    private var inMemoryAccessToken: AccessToken? = null

    fun saveAccessToken(accessToken: AccessToken) {
        inMemoryAccessToken = accessToken
    }

    fun getAccessToken(): AccessToken? = inMemoryAccessToken

    fun clear() {
        inMemoryAccessToken = null
    }
}