plugins {
    `android-common`
    `kotlinx-serialization`
    `kotlin-kapt`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.push.api))
    implementation(project(Modules.Feature.auth.api))
    implementation(project(Modules.Feature.main.api))
    implementation(project(Modules.Feature.messenger.api))

    implementation(Deps.Firebase.messaging)
    implementation(Deps.onesignal)

    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.network))
    implementation(project(Modules.Common.data))

    implementation(Deps.Glide.core)
    kapt(Deps.Glide.compiler)

}