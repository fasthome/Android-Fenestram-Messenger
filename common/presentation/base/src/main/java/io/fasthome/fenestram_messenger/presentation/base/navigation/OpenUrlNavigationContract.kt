package io.fasthome.fenestram_messenger.presentation.base.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.UnitResult
import io.fasthome.fenestram_messenger.presentation.base.ui.ActivityResultFragment
import kotlinx.parcelize.Parcelize


object OpenUrlNavigationContract :
    NavigationContract<OpenUrlNavigationContract.Params, UnitResult>(OpenUrlFragment::class) {
    @Parcelize
    data class Params(val url: String) : Parcelable
}

object OpenUrlActivityContract : ActivityResultContract<String, Unit>() {
    override fun createIntent(context: Context, url: String): Intent {

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url);
        }
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?) = Unit

}


class OpenUrlFragment :
    ActivityResultFragment<OpenUrlNavigationContract.Params, UnitResult, String, Unit>() {

    override val navigationContract = OpenUrlNavigationContract
    override val activityContract = OpenUrlActivityContract

    override val mapParams: (OpenUrlNavigationContract.Params) -> String = { it.url }
    override val mapResult: (Unit) -> UnitResult = { UnitResult }
}