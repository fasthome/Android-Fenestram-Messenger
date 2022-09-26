plugins {
    `android-common`
    `kotlin-kapt`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.uikit))

    implementation(Deps.AndroidX.core)
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.Android.material)

    implementation(Deps.Coroutines.core)

    implementation(Deps.Glide.core)
    kapt(Deps.Glide.compiler)

}