package io.fasthome.fenestram_messenger.util.links

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.TextView
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.R
import io.fasthome.fenestram_messenger.util.getPrintableText

fun TextView.applyLinks(vararg links: Link) {
    val builder = LinkBuilder.on(this)
    links.forEach { builder.addLink(it) }

    builder.build()
}

fun TextView.applyLinks(links: List<Link>) {
    LinkBuilder.on(this)
        .addLinks(links)
        .build()
}

fun TextView.addCommonLinks(onUserTagClicked:((String) -> Unit)?) {

    val webLinks: Link = Link(WEB_URL_PATTERN)
        .setOnLongClickListener {
            //TODO
        }
        .setOnClickListener { clickedText ->
            val url = if (!clickedText.startsWith("http://") && !clickedText.startsWith("https://"))
                "http://$clickedText" else clickedText
            val uri = Uri.parse(url)
            startCommonLinkIntent(context, uri)
        }

    val emailLinks: Link = Link(EMAIL_ADDRESS_PATTERN)
        .setOnLongClickListener {
            //TODO
        }
        .setOnClickListener { clickedText ->
            val uri = Uri.parse("mailto:$clickedText")
            startCommonLinkIntent(context, uri)
        }

    val phoneLinks: Link = Link(PHONE_PATTERN)
        .setOnLongClickListener {
            //TODO
        }
        .setOnClickListener { clickedText ->
            val uri = Uri.parse("tel:$clickedText")
            startCommonLinkIntent(context, uri)
        }

        val userTagLinks: Link = Link(USER_TAG_PATTERN)
        .setOnClickListener {
            onUserTagClicked?.invoke(it)
        }

    applyLinks(webLinks, emailLinks, phoneLinks, userTagLinks)
}

fun String.getNicknameFromLink() = this.substring(1)

private fun startCommonLinkIntent(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, uri)
    try {
        context.startActivity(
            Intent.createChooser(
                intent, context.getPrintableText(PrintableText.StringResource(R.string.open_with))
            )
        )
    } catch (e: Exception) {
        Log.d("Links Extension", e.toString())
    }
}