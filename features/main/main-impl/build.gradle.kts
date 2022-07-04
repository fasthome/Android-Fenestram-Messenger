plugins {
    `android-common`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.main.api))

    implementation(project(Modules.Common.core))
//    api(project(Modules.Common.di))
    implementation(Deps.Koin.core)
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))

}