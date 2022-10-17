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


object OpenFileNavigationContract :
    NavigationContract<OpenFileNavigationContract.Params, UnitResult>(OpenFileFragment::class) {
    @Parcelize
    data class Params(val uri: Uri) : Parcelable
}

object OpenFileActivityContract : ActivityResultContract<Uri, Unit>() {
    override fun createIntent(context: Context, uri: Uri): Intent {

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, context.contentResolver.getType(uri))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?) = Unit

}


class OpenFileFragment :
    ActivityResultFragment<OpenFileNavigationContract.Params, UnitResult, Uri, Unit>() {

    override val navigationContract = OpenFileNavigationContract
    override val activityContract = OpenFileActivityContract

    override val mapParams: (OpenFileNavigationContract.Params) -> Uri = { it.uri }
    override val mapResult: (Unit) -> UnitResult = { UnitResult }
}