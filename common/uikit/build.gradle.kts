plugins {
    `android-common`
    `kotlin-kapt`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Common.util))

    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.AndroidX.fragment)
    implementation(Deps.AndroidX.core)
    implementation(Deps.Android.material)
    implementation(Deps.AndroidX.paging)
    implementation(Deps.flexbox)

    api(Deps.AndroidX.swipeRefreshLayout)
    api(Deps.adapterDelegates)
    api(Deps.Koin.core)
    api(Deps.Koin.android)
    api(Deps.Koin.test)
    api(Deps.Koin.workmanager)

    api(Deps.Glide.core)
    kapt(Deps.Glide.compiler)

    api(Deps.libphonenumber)
}