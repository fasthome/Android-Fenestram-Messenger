plugins {
    `android-common`
}

dependencies {
    implementation(project(Modules.Common.navigation))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.component))

    implementation(project(Modules.Feature.contacts.api))

    implementation(Deps.Coroutines.core)
}