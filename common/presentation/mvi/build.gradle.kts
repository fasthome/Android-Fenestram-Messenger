plugins {
    `android-common`
}

dependencies {
    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.navigation))

    implementation(Deps.AndroidX.core)
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.AndroidX.fragment)
    implementation(Deps.Android.material)

    implementation(Deps.Coroutines.core)

}