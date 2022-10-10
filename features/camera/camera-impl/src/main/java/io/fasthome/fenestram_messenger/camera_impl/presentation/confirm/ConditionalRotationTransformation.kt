package io.fasthome.fenestram_messenger.camera_impl.presentation.confirm

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.Rotate

class ConditionalRotationTransformation(degreesToRotate: Int) : Rotate(degreesToRotate) {
    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap =
        if (toTransform.width > toTransform.height) {
            super.transform(pool, toTransform, outWidth, outHeight)
        } else {
            toTransform
        }
}