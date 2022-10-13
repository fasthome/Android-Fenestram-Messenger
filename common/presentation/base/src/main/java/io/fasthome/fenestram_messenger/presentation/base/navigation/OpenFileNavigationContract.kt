package io.fasthome.fenestram_messenger.presentation.base.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.UnitResult
import io.fasthome.fenestram_messenger.presentation.base.ui.ActivityResultFragment
import kotlinx.parcelize.Parcelize
import java.io.File


object OpenFileNavigationContract :
    NavigationContract<OpenFileNavigationContract.Params, UnitResult>(OpenFileFragment::class) {
    @Parcelize
    data class Params(val path: String) : Parcelable
}

object OpenFileActivityContract : ActivityResultContract<String, Unit>() {
    override fun createIntent(context: Context, input: String?): Intent {
        val file =
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path + input)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(FileProvider.getUriForFile(context,context.packageName,file), context.contentResolver.getType(Uri.fromFile(file)))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?) = Unit

}


class OpenFileFragment :
    ActivityResultFragment<OpenFileNavigationContract.Params, UnitResult, String, Unit>() {

    override val navigationContract = OpenFileNavigationContract
    override val activityContract = OpenFileActivityContract

    override val mapParams: (OpenFileNavigationContract.Params) -> String = { it.path }
    override val mapResult: (Unit) -> UnitResult = { UnitResult }
}