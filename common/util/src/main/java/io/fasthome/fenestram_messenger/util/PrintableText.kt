package io.fasthome.fenestram_messenger.util

import android.content.Context
import android.content.res.Resources
import android.os.Parcelable
import android.widget.TextView
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

sealed class PrintableText : Parcelable {

    @Parcelize
    data class Raw(val s: String) : PrintableText()

    @Parcelize
    data class StringResource(
        @StringRes val resId: Int,
        val formatArgs: @RawValue List<Any?>,
    ) : PrintableText() {
        constructor(@StringRes resId: Int, vararg formatArgs: Any?)
                : this(resId, formatArgs.toList())
    }

    @Parcelize
    data class PluralResource(
        @PluralsRes val resId: Int,
        val quantity: Int,
        val formatArgs: @RawValue List<Any?>,
    ) : PrintableText() {
        constructor(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any?)
                : this(resId, quantity, formatArgs.toList())
    }

    companion object {
        val EMPTY = Raw("")
    }
}

private fun List<Any?>.resolveFormatArgs(resources: Resources): Array<Any?> = this
    .map {
        if (it is PrintableText) {
            resources.getPrintableText(it)
        } else {
            it
        }
    }
    .toTypedArray()

fun Resources.getPrintableText(printableText: PrintableText): String = when (printableText) {
    is PrintableText.Raw -> printableText.s
    is PrintableText.StringResource -> getString(
        printableText.resId,
        *printableText.formatArgs.resolveFormatArgs(this),
    )
    is PrintableText.PluralResource -> getQuantityString(
        printableText.resId,
        printableText.quantity,
        *printableText.formatArgs.resolveFormatArgs(this),
    )
}

fun Context.getPrintableText(printableText: PrintableText) =
    resources.getPrintableText(printableText)

fun Fragment.getPrintableText(printableText: PrintableText) =
    resources.getPrintableText(printableText)

fun Fragment.getPrintableTextOrNull(printableText: PrintableText?): String? {
    printableText ?: return null
    return resources.getPrintableText(printableText)
}

fun TextView.setPrintableText(printableText: PrintableText) {
    text = resources.getPrintableText(printableText)
}

fun TextView.setPrintableTextOrGone(printableText: PrintableText?) {
    isVisible = printableText != null
    text = printableText?.let(resources::getPrintableText)
}

fun getPrintableRawText(printableText: PrintableText): String {
    return if(printableText is PrintableText.Raw){
        printableText.s
    }else{
        error("Only for PrintableText.Raw!")
    }
}