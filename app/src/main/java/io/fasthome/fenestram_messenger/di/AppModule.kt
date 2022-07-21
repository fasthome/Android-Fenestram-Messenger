package io.fasthome.fenestram_messenger.di

import io.fasthome.component.di.ComponentModule
import io.fasthome.fenestram_messenger.auth_impl.di.AuthModule
import io.fasthome.fenestram_messenger.contacts_impl.di.ContactsModule
import io.fasthome.fenestram_messenger.core.di.DataModule
import io.fasthome.fenestram_messenger.main_impl.di.MainModule
import io.fasthome.fenestram_messenger.messenger_impl.di.MessengerModule
import io.fasthome.fenestram_messenger.profile_impl.di.ProfileModule
import io.fasthome.fenestram_messenger.settings_impl.di.SettingsModule
import io.fasthome.fenestram_messenger.debug_impl.di.DebugModule
import io.fasthome.network.di.NetworkModule
import org.koin.core.module.Module


object AppModule {

    operator fun invoke() = listOf(
        createFeatureModules(),
        PresentationModule(),
        listOf(EnvironmentModule()),
        NetworkModule(),
        DataModule(),
        listOf(ComponentModule())
    ).flatten()

    private fun createFeatureModules(): List<Module> = listOf(
        MainModule(),
        AuthModule(),
        MessengerModule(),
        ContactsModule(),
        SettingsModule(),
        ProfileModule(),
        DebugModule()
    ).flatten()
}