plugins {
    `android-common`
    `kotlinx-serialization`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.contacts.api))
    implementation(project(Modules.Feature.auth.api))
    implementation(project(Modules.Feature.messenger.api))

    implementation(project(Modules.Common.component))
    implementation(project(Modules.Common.data))
    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.network))
    implementation(project(Modules.Common.uikit))

    androidTestImplementation(Deps.Tests.androidxJunit)
    androidTestImplementation(Deps.Tests.espresso)
    androidTestImplementation(Deps.Tests.junit)

    api(Deps.maskara)
}