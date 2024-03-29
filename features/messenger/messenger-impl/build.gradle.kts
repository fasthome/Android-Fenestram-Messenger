plugins {
    `android-common`
    `kotlin-kapt`
    `kotlinx-serialization`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.messenger.api))
    implementation(project(Modules.Feature.profileGuest.api))
    implementation(project(Modules.Feature.auth.api))
    implementation(project(Modules.Feature.contacts.api))
    implementation(project(Modules.Feature.profile.api))
    implementation(project(Modules.Feature.camera.api))

    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.data))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.network))
    implementation(project(Modules.Common.component))
    implementation(project(Modules.Common.uikit))

    implementation(Deps.flexbox)
    implementation(Deps.AndroidX.paging)
    implementation(Deps.socketIO) {
        exclude(group = "org.json", module = "json")
    }
    kapt(Deps.Room.compiler)


}