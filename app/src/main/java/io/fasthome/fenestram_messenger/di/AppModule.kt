package io.fasthome.fenestram_messenger.di

import io.fasthome.component.di.ComponentModule
import io.fasthome.fenestram_messenger.app.di.BuildTypeModule
import io.fasthome.fenestram_messenger.app.di.EnvironmentModule
import io.fasthome.fenestram_messenger.auth_impl.di.AuthModule
import io.fasthome.fenestram_messenger.call_impl.di.CallModule
import io.fasthome.fenestram_messenger.camera_impl.di.CameraModule
import io.fasthome.fenestram_messenger.contacts_impl.di.ContactsModule
import io.fasthome.fenestram_messenger.core.di.CoreModule
import io.fasthome.fenestram_messenger.data.di.DataModule
import io.fasthome.fenestram_messenger.debug_impl.di.DebugModule
import io.fasthome.fenestram_messenger.group_guest_impl.di.GroupGuestModule
import io.fasthome.fenestram_messenger.main_impl.di.MainModule
import io.fasthome.fenestram_messenger.messenger_impl.di.MessengerModule
import io.fasthome.fenestram_messenger.onboarding_impl.di.OnboardingModule
import io.fasthome.fenestram_messenger.profile_guest_impl.di.ProfileGuestModule
import io.fasthome.fenestram_messenger.profile_impl.di.ProfileModule
import io.fasthome.fenestram_messenger.push_impl.di.PushModule
import io.fasthome.fenestram_messenger.settings_impl.di.SettingsModule
import io.fasthome.network.di.NetworkModule
import org.koin.core.module.Module


object AppModule {

    operator fun invoke() = listOf(
        createFeatureModules(),
        PresentationModule(),
        listOf(EnvironmentModule()),
        BuildTypeModule(),
        NetworkModule(),
        DataModule(),
        listOf(ComponentModule(), CoreModule())
    ).flatten()

    private fun createFeatureModules(): List<Module> = listOf(
        MainModule(),
        AuthModule(),
        MessengerModule(),
        ContactsModule(),
        SettingsModule(),
        ProfileModule(),
        ProfileGuestModule(),
        DebugModule(),
        GroupGuestModule(),
        OnboardingModule(),
        PushModule(),
        CameraModule(),
        CallModule()
    ).flatten()
}