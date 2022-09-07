package io.fasthome.fenestram_messenger

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun main() {
    val username = "Dmitry Popov" // todo: поменяй это на своё имя.
    val name = "push" // todo: поменяй это на название модуля.

    // название модуля в коде.
    val codeName = name
        .split("-")
        .joinToString(separator = "", transform = { it.replaceFirstChar(Char::uppercaseChar) })

    // название модуля в gradle файлах.
    val moduleGradleName = codeName.replaceFirstChar(Char::lowercaseChar)

    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

    val fileHeader = "/**\n" +
            " * Created by $username on $date.\n" +
            " */\n"

    val codePackagePrefix = name.replace('-', '_')
    val apiPackage = "io.fasthome.fenestram_messenger.${codePackagePrefix}_api"
    val implPackage = "io.fasthome.fenestram_messenger.${codePackagePrefix}_impl"

    val projectRoot = File("").absoluteFile

    File(projectRoot, "settings.gradle.kts").appendText(
        "include(\":features:$name:$name-api\")\n" +
                "include(\":features:$name:$name-impl\")\n"
    )

    val apiDir = File(projectRoot, "features/$name/$name-api")
        .also { it.mkdirs() }

    File(apiDir, ".gitignore").writeText("/build")

    File(apiDir, "build.gradle.kts").writeText(
        "plugins {\n" +
                "    `android-common`\n" +
                "}\n" +
                "\n" +
                "dependencies {\n" +
                "    implementation(project(Modules.Common.navigation))\n" +
                "    implementation(project(Modules.Common.util))\n" +
                "}"
    )

    val apiMainDir = File(apiDir, "src/main")
        .also { it.mkdirs() }

    File(apiMainDir, "AndroidManifest.xml").writeText(
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    package=\"$apiPackage\">\n" +
                "\n" +
                "</manifest>"
    )

    val apiJavaDir = File(apiMainDir, "java/" + apiPackage.replace(".", "/"))
        .also { it.mkdirs() }

    File(apiJavaDir, "${codeName}Feature.kt").writeText(
        fileHeader +
                "package $apiPackage\n" +
                "\n" +
                "interface ${codeName}Feature {\n" +
                "}"
    )

    val implDir = File(projectRoot, "features/$name/$name-impl")
        .also { it.mkdirs() }

    File(implDir, ".gitignore").writeText("/build")

    File(implDir, "build.gradle.kts").writeText(
        "plugins {\n" +
                "    `android-common`\n" +
                "}\n" +
                "\n" +
                "android {\n" +
                "    enableViewBinding()\n" +
                "}\n" +
                "\n" +
                "dependencies {\n" +
                "    implementation(project(Modules.Feature.$moduleGradleName.api))\n" +
                "\n" +
                "    implementation(project(Modules.Common.core))\n" +
                "    implementation(project(Modules.Common.di))\n" +
                "    implementation(project(Modules.Common.presentation))\n" +
                "    implementation(project(Modules.Common.util))\n" +
                "\n" +
                "}"
    )

    File(implDir, "consumer-rules.pro").writeText(
        "#KotlinX Serialization\n" +
                "-keepclassmembers class $implPackage.data.service.model.** {\n" +
                "    *** Companion;\n" +
                "}"
    )

    val implMainDir = File(implDir, "src/main")
        .also { it.mkdirs() }

    File(implMainDir, "AndroidManifest.xml").writeText(
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    package=\"$implPackage\">\n" +
                "\n" +
                "</manifest>"
    )

    val implJavaDir = File(implMainDir, "java/" + implPackage.replace(".", "/"))
        .also { it.mkdirs() }

    File(implJavaDir, "${codeName}FeatureImpl.kt").writeText(
        fileHeader +
                "package $implPackage\n" +
                "\n" +
                "import $apiPackage.${codeName}Feature\n" +
                "\n" +
                "class ${codeName}FeatureImpl : ${codeName}Feature {\n" +
                "}"
    )

    val diDir = File(implJavaDir, "di")
        .also { it.mkdirs() }

    File(diDir, "${codeName}Module.kt").writeText(
        fileHeader +
                "package $implPackage.di\n" +
                "\n" +
                "import org.koin.dsl.module\n" +
                "import $apiPackage.${codeName}Feature\n" +
                "import $implPackage.${codeName}FeatureImpl\n" +
                "import io.fasthome.fenestram_messenger.di.bindSafe\n" +
                "import io.fasthome.fenestram_messenger.di.factory\n" +
                "import io.fasthome.fenestram_messenger.di.single\n" +
                "import io.fasthome.fenestram_messenger.di.viewModel\n" +
                "\n" +
                "//TODO: add to AppModule.\n" +
                "object ${codeName}Module {\n" +
                "    operator fun invoke() = listOf(\n" +
                "        createFeatureModule(),\n" +
                "        createDataModule(),\n" +
                "        createDomainModule(),\n" +
                "        createPresentationModule(),\n" +
                "    )\n" +
                "\n" +
                "    private fun createFeatureModule() = module {\n" +
                "        factory(::${codeName}FeatureImpl) bindSafe ${codeName}Feature::class\n" +
                "    }\n" +
                "\n" +
                "    private fun createDataModule() = module {\n" +
                "    }\n" +
                "\n" +
                "    private fun createDomainModule() = module {\n" +
                "    }\n" +
                "\n" +
                "    private fun createPresentationModule() = module {\n" +
                "    }\n" +
                "}"
    )

    File(implJavaDir, "data").mkdir()
    File(implJavaDir, "data/repo_impl").mkdir()
    File(implJavaDir, "data/service").mkdir()
    File(implJavaDir, "data/service/model").mkdir()
    File(implJavaDir, "data/service/mapper").mkdir()
    File(implJavaDir, "data/service").mkdir()
    File(implJavaDir, "data/storage").mkdir()

    File(implJavaDir, "domain").mkdir()
    File(implJavaDir, "domain/entity").mkdir()
    File(implJavaDir, "domain/logic").mkdir()
    File(implJavaDir, "domain/repo").mkdir()

    File(implJavaDir, "presentation").mkdir()

    val modulesFile = File(projectRoot, "buildSrc/src/main/kotlin/Modules.kt")
    val appBuildGradleFile = File(projectRoot, "app/build.gradle.kts")
    println(
        """
        Done! Module created!
        To complete module:
        1. add one line to "object Features" in $modulesFile :
                val $moduleGradleName = FeatureTemplate("$name")
        2. add two lines to $appBuildGradleFile :
                implementation(project(Modules.Feature.$moduleGradleName.api))
                implementation(project(Modules.Feature.$moduleGradleName.impl))
        3. sync Gradle.
        4. check TODOs in generated files.
        5. use your module!
    """.trimIndent()
    )
}