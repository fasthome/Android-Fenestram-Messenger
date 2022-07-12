plugins {
    `android-common`
}

dependencies {
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.di))

    api(Deps.Ktor.core)
    api(Deps.Ktor.logging)
    implementation(Deps.Ktor.client)
    implementation(Deps.Ktor.serialization)
}