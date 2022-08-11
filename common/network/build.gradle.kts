plugins {
    `android-common`
    `kotlinx-serialization`
}

dependencies {
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.data))
    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.di))

    api(Deps.Ktor.core)
    api(Deps.Ktor.logging)
    implementation(Deps.Ktor.client)
    implementation(Deps.Ktor.serialization)

    api(Deps.serializationJson)

}