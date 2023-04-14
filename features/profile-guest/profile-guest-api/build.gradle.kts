plugins {
    `android-common`
}

dependencies {
    implementation(project(Modules.Feature.contacts.api))
    implementation(project(Modules.Common.navigation))
    implementation(project(Modules.Common.util))
}