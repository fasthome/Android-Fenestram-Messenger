import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec
import java.io.ByteArrayOutputStream

inline val PluginDependenciesSpec.`android-common`: PluginDependencySpec get() = id("commonAndroid")

inline val PluginDependenciesSpec.`google-services`: PluginDependencySpec get() = id("com.google.gms.google-services")

inline val PluginDependenciesSpec.crashlytics: PluginDependencySpec get() = id("com.google.firebase.crashlytics")

inline val PluginDependenciesSpec.appdistribution: PluginDependencySpec get() = id("com.google.firebase.appdistribution")

inline val PluginDependenciesSpec.autoversion: PluginDependencySpec get() = id("etr.gradle.autoversion")

inline val PluginDependenciesSpec.`kotlinx-serialization`: PluginDependencySpec get() = id("kotlinx-serialization")

fun LibraryExtension.enableViewBinding() {
    buildFeatures.viewBinding = true
}

fun ApplicationExtension.enableViewBinding() {
    buildFeatures.viewBinding = true
}

fun ApplicationBuildType.booleanBuildConfigField(name: String, value: Boolean) {
    buildConfigField("boolean", name, "$value")
}

fun ApplicationDefaultConfig.stringBuildConfigField(name: String, value: String) {
    buildConfigField("String", name, "\"$value\"")
}

fun ApplicationBuildType.stringBuildConfigField(name: String, value: String) {
    buildConfigField("String", name, "\"$value\"")
}

fun ApplicationDefaultConfig.longBuildConfigField(name: String, value: Long) {
    buildConfigField("long", name, "${value}L")
}

fun getBooleanProperty(name: String, defaultValue: Boolean): Boolean {
    return when (val value = System.getProperty(name)) {
        null -> defaultValue
        "false" -> false
        "true" -> true
        else -> error("Invalid value '$value' for boolean property '$name'")
    }
}
