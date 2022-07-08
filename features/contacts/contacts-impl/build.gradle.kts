plugins {
    `android-common`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.contacts.api))
    implementation(project(Modules.Common.component))

    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))

}