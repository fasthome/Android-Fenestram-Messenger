plugins {
    `android-common`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.settings.api))
    implementation(project(Modules.Feature.auth.api))

    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.uikit))
    implementation(project(Modules.Common.data))
    implementation(project(Modules.Common.network))

}