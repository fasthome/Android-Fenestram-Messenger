package io.fasthome.fenestram_messenger.core.di

import io.fasthome.fenestram_messenger.core.data.KeyValueStorage
import io.fasthome.fenestram_messenger.core.data.StorageQualifier
import io.fasthome.fenestram_messenger.core.prefs.InMemoryKeyValueStorage
import io.fasthome.fenestram_messenger.core.prefs.PreferenceKeyValueStorage
import io.fasthome.fenestram_messenger.di.bindSafe
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import io.fasthome.fenestram_messenger.di.single

object DataModule {
    operator fun invoke(): List<Module> =
        listOf(
            createKeyValue(),
        )

    private fun createKeyValue() = module {
        single(
            InMemoryKeyValueStorage::Factory,
            named(StorageQualifier.InMemory)
        ) bindSafe KeyValueStorage.Factory::class

        single(
            PreferenceKeyValueStorage::SimpleFactory,
            named(StorageQualifier.Simple)
        ) bindSafe KeyValueStorage.Factory::class

        single(
            PreferenceKeyValueStorage::SecureFactory,
            named(StorageQualifier.Secure)
        ) bindSafe KeyValueStorage.Factory::class
    }

}