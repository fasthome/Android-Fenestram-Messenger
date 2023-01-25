package io.fasthome.fenestram_messenger.auth_impl.domain.logic

import io.fasthome.fenestram_messenger.data.CoreStorage
import io.fasthome.fenestram_messenger.data.UserStorage
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.network.tokens.TokensRepo

class ClearUserDataUseCase(
    private val tokensRepo: TokensRepo,
    private val userStorage: UserStorage,
    private val coreStorage: CoreStorage,
    private val messengerFeature: MessengerFeature
) {
    suspend fun invoke() {
        userStorage.clearPrefs()
        coreStorage.clearPrefs()
        messengerFeature.clearChats()
        messengerFeature.clearFileStorage()
        tokensRepo.clearTokens()
    }
}