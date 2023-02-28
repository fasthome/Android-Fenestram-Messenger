object Modules {

    const val app = ":app"

    object Common {
        const val core = ":common:core"
        const val data = ":common:data"
        const val util = ":common:util"
        const val di = ":common:di"
        const val presentation = ":common:presentation:base"
        const val navigation = ":common:presentation:navigation"
        const val mvi = ":common:presentation:mvi"
        const val component = ":common:component"
        const val network = ":common:network"
        const val uikit = ":common:uikit"
    }

    class FeatureTemplate(name: String) {
        val api = ":features:$name:$name-api"
        val impl = ":features:$name:$name-impl"
    }

    object Feature {
        val onboarding = FeatureTemplate("onboarding")
        val main = FeatureTemplate("main")
        val auth = FeatureTemplate("auth")
        val contacts = FeatureTemplate("contacts")
        val messenger = FeatureTemplate("messenger")
        val profile = FeatureTemplate("profile")
        val settings = FeatureTemplate("settings")
        val profileGuest = FeatureTemplate("profile-guest")
        val debug = FeatureTemplate("debug")
        val groupGuest = FeatureTemplate("group-guest")
        val push = FeatureTemplate("push")
        val camera = FeatureTemplate("camera")
        val call = FeatureTemplate("call")
        val authAd = FeatureTemplate("auth-ad")
    }
}