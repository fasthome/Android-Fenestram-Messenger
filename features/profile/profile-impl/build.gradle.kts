plugins {
    id("kotlin-android")
    `android-common`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.profile.api))

    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

}