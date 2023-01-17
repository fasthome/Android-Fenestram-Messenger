plugins {
    kotlin("android")
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val obfuscationEnabled = getBooleanProperty("obfuscationEnabled", false)

android {
    compileSdk = Config.compileSdk
    buildToolsVersion = Config.buildToolsVersion

    defaultConfig {
        applicationId = Config.applicationId
        minSdkPreview = Config.minSdk
        targetSdk = Config.targetSdk
        versionCode = Config.versionCode
        versionName = Config.versionName


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName(BuildType.release) {
            booleanBuildConfigField("IS_DEBUG", false)

            stringBuildConfigField("MAIN_API_BASE_URL_DEV", "http://dev.hooliphone.ru/")
            stringBuildConfigField("MAIN_API_BASE_URL_PROD", "http://hooliphone.ru/")

            stringBuildConfigField("ONESIGNAL_APP_ID", "977e9b8a-5cf3-401b-b801-3c62e346cfde")
            stringBuildConfigField("INSTABUG_APP_TOKEN_BETA", "5622f3685b90aca197ee34b9b3a612fb")

            stringBuildConfigField("DEV_API_VERSION", "v2")
            stringBuildConfigField("PROD_API_VERSION", "v2")

            stringBuildConfigField("POLICY_RULES_URL", "http://37.140.197.223/storage/PrivacyPolicy.html")

            isMinifyEnabled = obfuscationEnabled
            isShrinkResources = obfuscationEnabled
        }
        getByName(BuildType.debug) {
            booleanBuildConfigField("IS_DEBUG", true)

            stringBuildConfigField("MAIN_API_BASE_URL_DEV", "http://dev.hooliphone.ru/")
            stringBuildConfigField("MAIN_API_BASE_URL_PROD", "http://hooliphone.ru/")
            stringBuildConfigField("ONESIGNAL_APP_ID", "977e9b8a-5cf3-401b-b801-3c62e346cfde")
            stringBuildConfigField("INSTABUG_APP_TOKEN_BETA", "5622f3685b90aca197ee34b9b3a612fb")

            stringBuildConfigField("POLICY_RULES_URL", "http://37.140.197.223/storage/PrivacyPolicy.html")

            stringBuildConfigField("DEV_API_VERSION", "v2")
            stringBuildConfigField("PROD_API_VERSION", "v2")
            isDebuggable = true
            isMinifyEnabled = obfuscationEnabled
            isShrinkResources = obfuscationEnabled
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    enableViewBinding()
}

dependencies {
    implementation(Deps.kotlin)

    implementation(project(Modules.Common.core))
    implementation(project(Modules.Common.data))
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.presentation))
    implementation(project(Modules.Common.mvi))
    implementation(project(Modules.Common.navigation))
    implementation(project(Modules.Common.util))
    implementation(project(Modules.Common.component))
    implementation(project(Modules.Common.network))

    implementation(project(Modules.Feature.main.api))
    implementation(project(Modules.Feature.main.impl))

    implementation(project(Modules.Feature.auth.api))
    implementation(project(Modules.Feature.auth.impl))

    implementation(project(Modules.Feature.contacts.api))
    implementation(project(Modules.Feature.contacts.impl))

    implementation(project(Modules.Feature.messenger.api))
    implementation(project(Modules.Feature.messenger.impl))

    implementation(project(Modules.Feature.profile.api))
    implementation(project(Modules.Feature.profile.impl))

    implementation(project(Modules.Feature.settings.api))
    implementation(project(Modules.Feature.settings.impl))

    implementation(project(Modules.Feature.profileGuest.api))
    implementation(project(Modules.Feature.profileGuest.impl))

    implementation(project(Modules.Feature.debug.api))
    implementation(project(Modules.Feature.debug.impl))

    implementation(project(Modules.Feature.groupGuest.api))
    implementation(project(Modules.Feature.groupGuest.impl))

    implementation(project(Modules.Feature.push.api))
    implementation(project(Modules.Feature.push.impl))

    implementation(project(Modules.Feature.onboarding.api))
    implementation(project(Modules.Feature.onboarding.impl))

    implementation(project(Modules.Feature.camera.api))
    implementation(project(Modules.Feature.camera.impl))

    implementation(project(Modules.Feature.call.api))
    implementation(project(Modules.Feature.call.impl))

    implementation(Deps.AndroidX.core)
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.AndroidX.fragment)
    implementation(Deps.AndroidX.coordinatorLayout)
    implementation(Deps.AndroidX.constraintLayout)
    implementation(Deps.AndroidX.paging)
    implementation(Deps.AndroidX.navigationFragment)
    implementation(Deps.AndroidX.navigationUi)
    implementation(Deps.AndroidX.swipeRefreshLayout)
    implementation(Deps.AndroidX.legacy)

    implementation(Deps.Android.material)

    implementation(Deps.adapterDelegates)
    implementation(Deps.androidEmbedded)

    implementation(Deps.Firebase.crashlitycs)
    implementation(Deps.Firebase.analitycs)
    implementation(Deps.onesignal)

    implementation(Deps.lottie)
    implementation(Deps.gms)
    implementation(Deps.playCore)
    implementation(Deps.gson)
    implementation(Deps.materialCalendar)

    implementation(Deps.Koin.core)

    implementation(Deps.workRuntime)

    implementation(Deps.instaBug)

    androidTestImplementation(Deps.Tests.androidxJunit)
    androidTestImplementation(Deps.Tests.espresso)
    androidTestImplementation(Deps.Tests.junit)

    coreLibraryDesugaring(Deps.Tools.desugar)
}
