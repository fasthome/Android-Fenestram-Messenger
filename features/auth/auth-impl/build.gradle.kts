plugins {
    `android-common`
    `kotlinx-serialization`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.auth.api))
    implementation(project(Modules.Feature.profile.api))

    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.data))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.network))
    implementation(project(Modules.Common.component))

    implementation(Deps.Coil.coil)
    implementation(Deps.Coil.base)
    implementation(Deps.Coil.svg)
    implementation(Deps.Firebase.messaging)

    api(Deps.maskara)
    implementation("io.michaelrocks:libphonenumber-android:8.12.52")
}