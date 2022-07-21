package io.fasthome.fenestram_messenger.auth_impl.domain.logic

import io.fasthome.network.tokens.TokensRepo

class ClearUserDataUseCase(
    private val tokensRepo: TokensRepo,
) {
    suspend fun invoke() {
        tokensRepo.clearTokens()
    }
}