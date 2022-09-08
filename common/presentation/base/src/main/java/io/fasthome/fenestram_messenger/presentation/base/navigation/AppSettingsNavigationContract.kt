package io.fasthome.fenestram_messenger.presentation.base.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.UnitResult
import io.fasthome.fenestram_messenger.presentation.base.ui.ActivityResultFragment

object AppSettingsNavigationContract :
    NavigationContract<NoParams, UnitResult>(AppSettingsFragment::class)

object AppSettingsActivityResultContract : ActivityResultContract<Unit, Unit>() {
    override fun createIntent(context: Context, input: Unit?): Intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null),
    )

    override fun parseResult(resultCode: Int, intent: Intent?) = Unit
}

class AppSettingsFragment : ActivityResultFragment<NoParams, UnitResult, Unit, Unit>() {
    override val navigationContract = AppSettingsNavigationContract
    override val activityContract = AppSettingsActivityResultContract
    override val mapParams: (NoParams) -> Unit = { Unit }
    override val mapResult: (Unit) -> UnitResult = { UnitResult }
}