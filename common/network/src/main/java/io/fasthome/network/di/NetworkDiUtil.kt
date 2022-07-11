package io.fasthome.network.di

import io.fasthome.network.client.NetworkClientFactory
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

inline fun <reified P0, reified R> Module.singleAuthorizedService(
    crossinline provider: (P0) -> R,
    qualifier: Qualifier? = null,
) = singleService(NetworkClientFactoryQualifier.Authorized, provider, qualifier)

inline fun <reified P0, reified P1, reified R> Module.singleAuthorizedService(
    crossinline provider: (P0, P1) -> R,
    qualifier: Qualifier? = null,
) = singleService(NetworkClientFactoryQualifier.Authorized, provider, qualifier)

inline fun <reified P0, reified P1, reified P2, reified R> Module.singleAuthorizedService(
    crossinline provider: (P0, P1, P2) -> R,
    qualifier: Qualifier? = null,
) = singleService(NetworkClientFactoryQualifier.Authorized, provider, qualifier)

inline fun <reified P0, reified P1, reified P2, reified P3, reified R> Module.singleAuthorizedService(
    crossinline provider: (P0, P1, P2, P3) -> R,
    qualifier: Qualifier? = null,
) = singleService(NetworkClientFactoryQualifier.Authorized, provider, qualifier)


inline fun <reified P0, reified R> Module.singleService(
    networkClientFactoryQualifier: NetworkClientFactoryQualifier,
    crossinline provider: (P0) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) {
    provider(
        getQualifiedOrNot(networkClientFactoryQualifier),
    )
}

inline fun <reified P0, reified P1, reified R> Module.singleService(
    networkClientFactoryQualifier: NetworkClientFactoryQualifier,
    crossinline provider: (P0, P1) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) {
    provider(
        getQualifiedOrNot(networkClientFactoryQualifier),
        getQualifiedOrNot(networkClientFactoryQualifier),
    )
}

inline fun <reified P0, reified P1, reified P2, reified R> Module.singleService(
    networkClientFactoryQualifier: NetworkClientFactoryQualifier,
    crossinline provider: (P0, P1, P2) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) {
    provider(
        getQualifiedOrNot(networkClientFactoryQualifier),
        getQualifiedOrNot(networkClientFactoryQualifier),
        getQualifiedOrNot(networkClientFactoryQualifier),
    )
}

inline fun <reified P0, reified P1, reified P2, reified P3, reified R> Module.singleService(
    networkClientFactoryQualifier: NetworkClientFactoryQualifier,
    crossinline provider: (P0, P1, P2, P3) -> R,
    qualifier: Qualifier? = null,
) = single(qualifier = qualifier) {
    provider(
        getQualifiedOrNot(networkClientFactoryQualifier),
        getQualifiedOrNot(networkClientFactoryQualifier),
        getQualifiedOrNot(networkClientFactoryQualifier),
        getQualifiedOrNot(networkClientFactoryQualifier),
    )
}

@PublishedApi
internal inline fun <reified T> Scope.getQualifiedOrNot(
    networkClientFactoryQualifier: NetworkClientFactoryQualifier,
): T = if (T::class == NetworkClientFactory::class) {
    get(named(networkClientFactoryQualifier))
} else {
    get()
}