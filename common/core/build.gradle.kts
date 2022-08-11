plugins {
    `android-common`
}

dependencies {
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.di))

    implementation(Deps.AndroidX.core)
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.Android.material)

    implementation(Deps.Coroutines.core)

//    implementation(Deps.Firebase.firestore)

}