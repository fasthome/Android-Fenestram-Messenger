package io.fasthome.fenestram_messenger.uikit.custom_view

import android.content.Context
import android.graphics.*
import android.widget.ImageView
import io.fasthome.fenestram_messenger.uikit.R
import io.fasthome.fenestram_messenger.util.android.color
import io.fasthome.fenestram_messenger.util.dp


fun ImageView.emptyAvatarWithUsername(username: String?): Bitmap? {
    val width = if (this.width == 0) 64.dp else this.width
    val height = if (this.height == 0) 64.dp else this.height

    val firstLetter = if (username.isNullOrEmpty()) "" else username.first().toString().uppercase()

    val sumOfCodeSymbols = (username ?: "U").toList().sumOf {
        it.code
    }

    val indexGradient = sumOfCodeSymbols % gradientPairs.size

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val backgroundPaint = Paint()

    val gradient = Pair(
        context.color(gradientPairs[indexGradient].first),
        context.color(gradientPairs[indexGradient].second)
    )

    backgroundPaint.shader = LinearGradient(
        0f,
        0f,
        0f,
        height.toFloat(),
        gradient.first,
        gradient.second,
        Shader.TileMode.MIRROR
    )

    canvas.drawCircle(
        (width / 2).toFloat(),
        (height / 2).toFloat(),
        (width / 2).toFloat(),
        backgroundPaint
    )

    val textPaint = Paint()
    textPaint.color = Color.WHITE
    textPaint.textSize = (canvas.height / 2).toFloat()
    textPaint.textAlign = Paint.Align.CENTER

    canvas.drawText(
        firstLetter,
        (canvas.width / 2).toFloat(),
        ((canvas.height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)),
        textPaint
    )

    return bitmap
}

private fun Context.getRandomGradientPair(): Pair<Int, Int> {
    val pair = gradientPairs.random()
    return Pair(color(pair.first), color(pair.second))
}

private val gradientPairs = listOf(
    Pair(R.color.profile_gradient_1_1, R.color.profile_gradient_1_2),
    Pair(R.color.profile_gradient_2_1, R.color.profile_gradient_2_2),
    Pair(R.color.profile_gradient_3_1, R.color.profile_gradient_3_2),
    Pair(R.color.profile_gradient_4_1, R.color.profile_gradient_4_2),
    Pair(R.color.profile_gradient_5_1, R.color.profile_gradient_5_2),
    Pair(R.color.profile_gradient_6_1, R.color.profile_gradient_6_2),
    Pair(R.color.profile_gradient_7_1, R.color.profile_gradient_7_2),
    Pair(R.color.profile_gradient_8_1, R.color.profile_gradient_8_2),
    Pair(R.color.profile_gradient_9_1, R.color.profile_gradient_9_2),
    Pair(R.color.profile_gradient_10_1, R.color.profile_gradient_10_2),
    Pair(R.color.profile_gradient_11_1, R.color.profile_gradient_11_2),
    Pair(R.color.profile_gradient_12_1, R.color.profile_gradient_12_2),
    Pair(R.color.profile_gradient_13_1, R.color.profile_gradient_13_2),
)