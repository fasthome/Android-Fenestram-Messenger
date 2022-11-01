package io.fasthome.fenestram_messenger.util.links

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.widget.TextView
import java.util.regex.Pattern

class LinkBuilder {

    private var context: Context? = null

    private val links = ArrayList<Link>()

    private var textView: TextView? = null
    private var text: CharSequence? = null
    private var spannable: SpannableString? = null

    fun setTextView(textView: TextView): LinkBuilder {
        this.textView = textView
        return setText(textView.text)
    }

    fun setText(text: CharSequence): LinkBuilder {
        this.text = text
        return this
    }

    fun setContext(context: Context): LinkBuilder {
        this.context = context
        return this
    }

    fun addLink(link: Link): LinkBuilder {
        this.links.add(link)
        return this
    }

    fun addLinks(links: List<Link>): LinkBuilder {
        if (links.isEmpty()) {
            throw IllegalArgumentException("link list is empty")
        }

        this.links.addAll(links)
        return this
    }

    fun build(): CharSequence? {
        turnPatternsToLinks()
        if (links.size == 0) {
            return null
        }

        for (link in links) {
            addLinkToSpan(link)
        }

        textView!!.text = spannable
        addLinkMovementMethod()

        return spannable
    }

    private fun addLinkToSpan(link: Link) {
        if (spannable == null) {
            spannable = SpannableString.valueOf(text)
        }

        addLinkToSpan(spannable!!, link)
    }

    private fun addLinkToSpan(s: Spannable, link: Link) {
        val pattern = Pattern.compile(Pattern.quote(link.text!!))
        val matcher = pattern.matcher(text!!)

        while (matcher.find()) {
            val start = matcher.start()
            if (start >= 0 && link.text != null) {
                val end = start + link.text!!.length
                applyLink(link, Range(start, end), s)
            }
        }
    }

    private fun addLinkMovementMethod() {
        if (textView == null) {
            return
        }

        val m = textView!!.movementMethod
        if (m == null || m !is TouchableMovementMethod) {
            if (textView!!.linksClickable) {
                textView!!.movementMethod = TouchableMovementMethod.instance
            }
        }
    }

    private fun applyLink(link: Link, range: Range, text: Spannable) {
        val existingSpans = text.getSpans(range.start, range.end, TouchableSpan::class.java)
        if (existingSpans.isEmpty()) {
            val span = TouchableSpan(context!!, link)
            text.setSpan(span, range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            var newSpanConsumesAllOld = true
            for (span in existingSpans) {
                val start = spannable!!.getSpanStart(span)
                val end = spannable!!.getSpanEnd(span)
                if (range.start > start || range.end < end) {
                    newSpanConsumesAllOld = false
                    break
                } else {
                    text.removeSpan(span)
                }
            }

            if (newSpanConsumesAllOld) {
                val span = TouchableSpan(context!!, link)
                text.setSpan(span, range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun turnPatternsToLinks() {
        var size = links.size
        var i = 0

        while (i < size) {
            if (links[i].pattern != null) {
                addLinksFromPattern(links[i])

                links.removeAt(i)
                size--
            } else {
                i++
            }
        }
    }

    private fun addLinksFromPattern(linkWithPattern: Link) {
        val pattern = linkWithPattern.pattern
        val m = pattern?.matcher(text!!) ?: return

        while (m.find()) {
            links.add(
                Link(linkWithPattern).setText(
                    text!!.subSequence(m.start(), m.end()).toString()
                )
            )
        }
    }

    private class Range(var start: Int, var end: Int)

    companion object {
        @JvmStatic
        fun on(tv: TextView): LinkBuilder {
            return LinkBuilder()
                .setContext(tv.context)
                .setTextView(tv)
        }
    }
}