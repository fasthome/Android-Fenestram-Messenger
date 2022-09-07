plugins {
    `android-common`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.AndroidX.fragment)
    implementation(Deps.AndroidX.core)
    implementation(Deps.Android.material)

    implementation(project(Modules.Common.util))
    api(Deps.Koin.core)
    api(Deps.Koin.android)
    api(Deps.Koin.test)
    api(Deps.Koin.workmanager)
}