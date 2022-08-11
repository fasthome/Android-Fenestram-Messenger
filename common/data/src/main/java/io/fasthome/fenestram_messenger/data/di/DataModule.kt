package io.fasthome.fenestram_messenger.data.di

import io.fasthome.fenestram_messenger.data.FileSystemInterface
import io.fasthome.fenestram_messenger.data.FileSystemInterfaceImpl
import io.fasthome.fenestram_messenger.data.KeyValueStorage
import io.fasthome.fenestram_messenger.data.StorageQualifier
import io.fasthome.fenestram_messenger.data.prefs.InMemoryKeyValueStorage
import io.fasthome.fenestram_messenger.data.prefs.PreferenceKeyValueStorage
import io.fasthome.fenestram_messenger.di.bindSafe
import io.fasthome.fenestram_messenger.di.single
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

object DataModule {
    operator fun invoke(): List<Module> =
        listOf(
            createKeyValue(),
            createFile()
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

    private fun createFile() = module{
        single(::FileSystemInterfaceImpl) bindSafe FileSystemInterface::class
    }

}