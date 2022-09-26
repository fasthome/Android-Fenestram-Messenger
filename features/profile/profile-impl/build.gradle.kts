plugins {
    `android-common`
    `kotlinx-serialization`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.profile.api))
    implementation(project(Modules.Feature.settings.api))

    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.component))
    implementation(project(Modules.Common.network))
    implementation(project(Modules.Common.uikit))
}