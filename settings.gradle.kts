dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "Fenestram-Messenger"
include(":app")
include(":common:core")
include(":common:util")
include(":common:presentation:base")
include(":common:presentation:mvi")
include(":common:presentation:navigation")
include(":common:di")
include(":features:main:main-api")
include(":features:main:main-impl")
include(":features:auth:auth-api")
include(":features:auth:auth-impl")
include(":features:contacts:contacts-api")
include(":features:contacts:contacts-impl")
include(":features:messenger:messenger-api")
include(":features:messenger:messenger-impl")
include(":features:profile:profile-api")
include(":features:profile:profile-impl")
include(":common:component")
include(":common:network")
include(":features:settings:settings-api")
include(":features:settings:settings-impl")
include(":features:debug:debug-api")
include(":features:debug:debug-impl")
