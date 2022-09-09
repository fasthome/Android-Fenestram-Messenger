plugins {
    `android-common`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.profileGuest.api))
    implementation(project(Modules.Feature.groupGuest.api))
    implementation(project(Modules.Feature.messenger.api))

    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.uikit))
    implementation(project(Modules.Common.network))

}