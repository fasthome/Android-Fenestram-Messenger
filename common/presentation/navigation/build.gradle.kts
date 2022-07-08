plugins {
    `android-common`
}

dependencies {
    implementation(Deps.AndroidX.core)
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.AndroidX.fragment)
    implementation(Deps.AndroidX.lifecycleCommon)

    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.di))

    api(Deps.cicerone)
}