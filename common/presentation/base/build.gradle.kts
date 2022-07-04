plugins {
    `android-common`
}

android {
    enableViewBinding()
}

dependencies {
    api(project(Modules.Common.navigation))
    api(project(Modules.Common.mvi))
    api(project(Modules.Common.di))

    implementation(project(Modules.Common.util))

    api(Deps.AndroidX.core)
    api(Deps.AndroidX.appcompat)
    api(Deps.AndroidX.fragment)
    api(Deps.AndroidX.lifecycleCommon)
    api(Deps.Android.material)
    api(Deps.Coroutines.core)

}