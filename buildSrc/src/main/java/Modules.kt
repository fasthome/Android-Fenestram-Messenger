object Modules {

    const val app = ":app"

    object Common {
        const val core = ":common:core"
        const val util = ":common:util"
        const val di = ":common:di"
        const val presentation = ":common:presentation:base"
        const val navigation = ":common:presentation:navigation"
        const val mvi = ":common:presentation:mvi"
    }

    class FeatureTemplate(name: String) {
        val api = ":features:$name:$name-api"
        val impl = ":features:$name:$name-impl"
    }

    object Feature {
        val main = FeatureTemplate("main")
        val auth = FeatureTemplate("auth")
        val contacts = FeatureTemplate("contacts")
        val messenger = FeatureTemplate("messenger")
        val profile = FeatureTemplate("messenger")
    }
}