plugins {
    `android-common`
}

dependencies {
    implementation(project(Modules.Feature.call.api))

    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))

}