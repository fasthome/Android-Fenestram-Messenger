/**
 * Precompiled [commonAndroid.gradle.kts][CommonAndroid_gradle] script plugin.
 *
 * @see CommonAndroid_gradle
 */
class CommonAndroidPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("CommonAndroid_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
