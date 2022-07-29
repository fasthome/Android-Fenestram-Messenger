plugins {
    `android-common`
}

dependencies {
    implementation(project(Modules.Common.navigation))
    implementation(project(Modules.Common.util))
    implementation(Deps.Coroutines.core)
}