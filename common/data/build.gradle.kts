plugins {
    `android-common`
    `kotlin-kapt`
}

dependencies {
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.core))

    implementation(Deps.AndroidX.crypto)

}