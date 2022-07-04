plugins {
    kotlin("android")
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    id("kotlin-parcelize")
//    id("com.google.gms.google-services")
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

            isMinifyEnabled = obfuscationEnabled
            isShrinkResources = obfuscationEnabled
        }
        getByName(BuildType.debug) {
            booleanBuildConfigField("IS_DEBUG", true)

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
    implementation(project(Modules.Common.di))
    implementation(project(Modules.Common.mvi))
    implementation(project(Modules.Common.navigation))
    implementation(project(Modules.Common.util))

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

    implementation(Deps.Coil.coil)
    implementation(Deps.Coil.base)
    implementation(Deps.Coil.svg)

    implementation(Deps.lottie)
    implementation(Deps.gms)
    implementation(Deps.playCore)
    implementation(Deps.gson)
    implementation(Deps.materialCalendar)

    implementation(Deps.Koin.core)

    implementation(Deps.Squareup.retrofit)
    implementation(Deps.Squareup.gsonConverter)
    implementation(Deps.Squareup.rxadapter)
    implementation(Deps.Squareup.okhttp)
    implementation(Deps.Squareup.interceptor)

    androidTestImplementation(Deps.Tests.androidxJunit)
    androidTestImplementation(Deps.Tests.espresso)
    androidTestImplementation(Deps.Tests.junit)

    coreLibraryDesugaring(Deps.Tools.desugar)
}
