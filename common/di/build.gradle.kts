plugins {
    `android-common`
}

dependencies {
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.AndroidX.fragment)

    api(Deps.Koin.core)
    api(Deps.Koin.android)
    api(Deps.Koin.test)
    api(Deps.Koin.workmanager)
}