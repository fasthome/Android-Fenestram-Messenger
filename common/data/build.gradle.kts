plugins {
    `android-common`
    `kotlin-kapt`
}

dependencies {
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.core))

    implementation(Deps.AndroidX.crypto)

    api(Deps.Room.runtime)
    api(Deps.Room.ktx)
    kapt(Deps.Room.compiler)
}