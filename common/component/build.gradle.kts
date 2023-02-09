plugins {
    `android-common`
}

android {
    enableViewBinding()
}

dependencies {
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.data))
    implementation(project(Modules.Common.uikit))

    implementation(Deps.AndroidX.paging)
    implementation(Deps.AndroidX.core)
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.flexbox)

    api(Deps.AndroidX.Camera.core)
    api(Deps.AndroidX.Camera.camera2)
    api(Deps.AndroidX.Camera.lifecycle)
    api(Deps.AndroidX.Camera.view)

    implementation(project(Modules.Feature.auth.api))
}