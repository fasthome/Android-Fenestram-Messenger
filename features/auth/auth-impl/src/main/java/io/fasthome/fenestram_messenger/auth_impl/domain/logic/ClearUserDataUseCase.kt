package io.fasthome.fenestram_messenger.auth_impl.domain.logic

import io.fasthome.fenestram_messenger.camera_api.CameraFeature
import io.fasthome.fenestram_messenger.data.CoreStorage
import io.fasthome.fenestram_messenger.data.UserStorage
import io.fasthome.network.tokens.TokensRepo

class ClearUserDataUseCase(
    private val tokensRepo: TokensRepo,
    private val userStorage: UserStorage,
    private val coreStorage: CoreStorage,
    private val cameraFeature: CameraFeature
) {
    suspend fun invoke() {
        userStorage.clearPrefs()
        coreStorage.clearPrefs()
        cameraFeature.clearFileStorage()
        tokensRepo.clearTokens()
    }
}