plugins {
    `android-common`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Feature.debug.api))
    implementation(project(Modules.Feature.auth.api))
    implementation(project(Modules.Feature.messenger.impl))

    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.util))

    implementation(Deps.socketIO) {
        exclude(group = "org.json", module = "json")
    }
}