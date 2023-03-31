plugins {
    `android-common`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.tasks.api))
    implementation(project(Modules.Feature.contacts.api))

    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.uikit))
    implementation(project(Modules.Common.network))

    implementation(Deps.AndroidX.paging)

}