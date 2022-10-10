plugins {
    `android-common`
}

dependencies {
    implementation(project(Modules.Common.navigation))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.component))
    implementation(project(Modules.Common.uikit))

}