package io.fasthome.fenestram_messenger.push_impl.domain.entity

sealed interface DeviceTokenResult {
    object PlayServicesNotAvailable : DeviceTokenResult
    data class TokenReceivingError(val error: Throwable) : DeviceTokenResult
    data class TokenReceived(val deviceToken: String) : DeviceTokenResult
}