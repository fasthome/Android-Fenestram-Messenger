import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection

object Deps {

    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.JetBrains.kotlin}"

    object AndroidX {
        const val core = "androidx.core:core-ktx:${Versions.AndroidX.core}"
        const val appcompat = "androidx.appcompat:appcompat:${Versions.AndroidX.appcompat}"
        const val fragment = "androidx.fragment:fragment-ktx:${Versions.AndroidX.fragment}"
        const val coordinatorLayout =
            "androidx.coordinatorlayout:coordinatorlayout:${Versions.AndroidX.coordinatorLayout}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraintLayout}"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.AndroidX.swipeRefreshLayout}"

        const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.AndroidX.navigationVersion}"
        const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.AndroidX.navigationVersion}"
        const val paging = "androidx.paging:paging-runtime-ktx:3.1.0"
        const val legacy = "androidx.legacy:legacy-support-v4:1.0.0"
        const val lifecycleCommon =
            "androidx.lifecycle:lifecycle-common-java8:${Versions.AndroidX.lifecycle}"

        object Camera {
            const val core = "androidx.camera:camera-core:${Versions.AndroidX.camera}"
            const val camera2 = "androidx.camera:camera-camera2:${Versions.AndroidX.camera}"
            const val lifecycle = "androidx.camera:camera-lifecycle:${Versions.AndroidX.camera}"
            const val view = "androidx.camera:camera-view:${Versions.AndroidX.cameraView}"
        }
    }

    object Android {
        const val material = "com.google.android.material:material:${Versions.Android.material}"
    }

    const val inputMask = "com.redmadrobot:input-mask-android:${Versions.inputMask}"

    const val adapterDelegates = "com.hannesdorfmann:adapterdelegates4-kotlin-dsl-viewbinding:4.3.0"
    const val androidEmbedded = "com.journeyapps:zxing-android-embedded:4.3.0"

    object Firebase {
        const val crashlitycs = "com.google.firebase:firebase-crashlytics:18.2.7"
        const val analitycs = "com.google.firebase:firebase-analytics:20.0.2"
        const val firestore = "com.google.firebase:firebase-firestore-ktx:24.1.2"
    }

    object Moxy {
        const val moxy = "com.github.moxy-community:moxy:2.2.2"
        const val androidx ="com.github.moxy-community:moxy-androidx:${Versions.moxy}"
        const val ktx = "com.github.moxy-community:moxy-ktx:${Versions.moxy}"
        const val compiler = "com.github.moxy-community:moxy-compiler:${Versions.moxy}"
    }

    object Dagger {
        const val dagger = "com.google.dagger:dagger:2.38.1"
        const val android = "com.google.dagger:dagger-android:2.37"
        const val compiler = "com.google.dagger:dagger-compiler:2.37"
    }

    object Squareup {
        const val retrofit = "com.squareup.retrofit2:retrofit:2.9.0"
        const val gsonConverter = "com.squareup.retrofit2:converter-gson:2.9.0"
        const val rxadapter = "com.squareup.retrofit2:adapter-rxjava3:2.9.0"
        const val okhttp = "com.squareup.okhttp3:okhttp:4.9.0"
        const val interceptor = "com.squareup.okhttp3:logging-interceptor:4.9.0"
    }

    object Coil {
        const val coil = "io.coil-kt:coil:1.3.0"
        const val base = "io.coil-kt:coil-base:1.3.0"
        const val svg = "io.coil-kt:coil-svg:1.3.0"
    }

    object Room {
        const val runtime = "androidx.room:room-runtime:2.4.1"
        const val compiler = "androidx.room:room-compiler:2.4.1"
        const val ktx = "androidx.room:room-ktx:2.4.1"
    }

    object Tests {
        const val androidxJunit = "androidx.test.ext:junit:1.1.3"
        const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
        const val junit = "junit:junit:4.13.2"
    }

    const val gson = "com.google.code.gson:gson:2.8.6"
    const val lottie = "com.airbnb.android:lottie:4.1.0"
    const val chart = "com.github.PhilJay:MPAndroidChart:v3.1.0"
    const val eventBus = "org.greenrobot:eventbus:3.2.0"
    const val circleProgress = "com.budiyev.android:circular-progress-bar:1.2.2"

    const val gms = "com.google.android.gms:play-services-location:19.0.1"
    const val playCore = "com.google.android.play:core:1.10.3"
    const val onesignal = "com.onesignal:OneSignal:4.6.3"

    const val map = "org.osmdroid:osmdroid-android:6.1.6"

    const val materialCalendar = "com.github.prolificinteractive:material-calendarview:2.0.0"

    fun w3r2Sdk(project: Project): ConfigurableFileCollection =
        project.files("libs/ute_sdk_v3.1.5.aar")

    const val dfuForW3R2Sdk = "no.nordicsemi.android:dfu:1.12.1-beta01"

    object Tools {
        const val desugar = "com.android.tools:desugar_jdk_libs:${Versions.Tools.desugar}"
    }

    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1"

    }

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
        const val workmanager = "io.insert-koin:koin-androidx-workmanager:${Versions.koin}"
    }

    const val cicerone = "com.github.terrakok:cicerone:${Versions.cicerone}"

}