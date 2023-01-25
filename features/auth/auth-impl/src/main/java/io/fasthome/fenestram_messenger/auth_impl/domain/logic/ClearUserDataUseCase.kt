package io.fasthome.fenestram_messenger.auth_impl.domain.logic

import io.fasthome.fenestram_messenger.camera_api.CameraFeature
import io.fasthome.fenestram_messenger.data.CoreStorage
import io.fasthome.fenestram_messenger.data.UserStorage
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.network.tokens.TokensRepo

class ClearUserDataUseCase(
    private val tokensRepo: TokensRepo,
    private val userStorage: UserStorage,
    private val coreStorage: CoreStorage,
    private val messengerFeature: MessengerFeature,
    private val cameraFeature: CameraFeature
) {
    suspend fun invoke() {
        userStorage.clearPrefs()
        coreStorage.clearPrefs()
        messengerFeature.clearChats()
        cameraFeature.clearFileStorage()
        tokensRepo.clearTokens()
    }
}