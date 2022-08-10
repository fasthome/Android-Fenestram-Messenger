plugins {
    `android-common`
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


    implementation(Deps.Coil.coil)
    implementation(Deps.Coil.base)
    implementation(Deps.Coil.svg)
    implementation(project(mapOf("path" to ":features:settings:settings-impl")))
}