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
include(":features:profile-guest:profile-guest-api")
include(":features:profile-guest:profile-guest-impl")
include(":common:data")
include(":features:onboarding:onboarding-api")
include(":features:onboarding:onboarding-impl")
include(":features:group-guest:group-guest-api")
include(":features:group-guest:group-guest-impl")
include(":common:uikit")
include(":features:push:push-api")
include(":features:push:push-impl")
include(":features:camera:camera-api")
include(":features:camera:camera-impl")
include(":features:call:call-api")
include(":features:call:call-impl")
include(":features:auth-ad:auth-ad-api")
include(":features:auth-ad:auth-ad-impl")
include(":features:tasks:tasks-api")
include(":features:tasks:tasks-impl")
