plugins {
    `android-common`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.main.api))
    implementation(project(Modules.Feature.messenger.api))
    implementation(project(Modules.Feature.profile.api))
    implementation(project(Modules.Feature.contacts.api))
    implementation(project(Modules.Feature.debug.api))

    implementation(project(Modules.Common.core))
    api(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))
    implementation(project(mapOf("path" to ":features:profile:profile-api")))
}