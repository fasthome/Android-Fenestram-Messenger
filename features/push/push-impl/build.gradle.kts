plugins {
    `android-common`
    `kotlinx-serialization`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.push.api))
    implementation(project(Modules.Feature.auth.api))

    implementation(Deps.Firebase.messaging)

    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.network))
    implementation(project(Modules.Common.data))

}