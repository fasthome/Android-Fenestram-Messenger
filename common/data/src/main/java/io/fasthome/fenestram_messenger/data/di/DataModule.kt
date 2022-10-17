package io.fasthome.fenestram_messenger.data.di

import io.fasthome.fenestram_messenger.data.*
import io.fasthome.fenestram_messenger.data.core.CoreRepo
import io.fasthome.fenestram_messenger.data.core.CoreRepoImpl
import io.fasthome.fenestram_messenger.data.file.*
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
            createFile(),
            createExt()
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

        /***
         * todo Использовать хранилище с шифрованием для хранения токенов
         * сейчас при использовании его бывает краш при открытии приложения
         */
//        single(
//            PreferenceKeyValueStorage::SecureFactory,
//            named(StorageQualifier.Secure)
//        ) bindSafe KeyValueStorage.Factory::class

        single(
            ::CoreRepoImpl
        ) bindSafe CoreRepo::class
        single { CoreStorage(get(named(StorageQualifier.Simple))) }
    }

    private fun createFile() = module {
        single(::FileSystemInterfaceImpl) bindSafe FileSystemInterface::class

        single(
            SimpleFileStorage::FactoryFiles,
            named(StorageQualifier.Simple)
        ) bindSafe FileStorage.Factory::class
        single(::FileManagerImpl) bindSafe FileManager::class
        single(::DownloadFileManagerImpl) bindSafe DownloadFileManager::class
    }

    private fun createExt() = module {
        single(::StorageUrlConverter)
    }

}