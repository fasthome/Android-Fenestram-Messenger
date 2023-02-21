package io.fasthome.component.file_selector

import android.view.animation.Animation
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UriLoadableContent
import io.fasthome.fenestram_messenger.util.AnimationUtil

data class FileSelectorViewItem(
    val content: UriLoadableContent,
    var isChecked: Boolean = false,
    val cursorPosition: Int = 0,
) {
    fun getAnimation(): Animation {
        return if (isChecked) AnimationUtil.getScaleAnimation(
            1f,
            .95f
        ) else AnimationUtil.getScaleAnimation(.95f, 1f)
    }
}