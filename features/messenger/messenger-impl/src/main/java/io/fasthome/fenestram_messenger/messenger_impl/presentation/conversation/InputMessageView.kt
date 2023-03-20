/**
 * Created by Dmitry Popov on 01.02.2023.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UriLoadableContent

typealias InputContentListener = (Content) -> Unit
typealias SelectionChangedListener = (selStart: Int, selEnd: Int) -> Unit

class InputMessageView : AppCompatEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var inputContentListener: InputContentListener? = null
    private var selectionChangedListener: SelectionChangedListener? = null

    override fun onCreateInputConnection(editorInfo: EditorInfo): InputConnection? {
        val ic: InputConnection = super.onCreateInputConnection(editorInfo) ?: return null
        EditorInfoCompat.setContentMimeTypes(
            editorInfo, arrayOf(
                "image/jpeg",
                "image/jpg",
                "image/png",
                "image/gif",
                "image/*"
            )
        )

        val callback =
            InputConnectionCompat.OnCommitContentListener { inputContentInfo, flags, opts ->
                val lacksPermission = (flags and
                        InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && lacksPermission) {
                    try {
                        inputContentInfo.requestPermission()
                    } catch (e: Exception) {
                        return@OnCommitContentListener false
                    }
                }
                inputContentListener?.invoke(UriLoadableContent(inputContentInfo.contentUri))
                true
            }
        return InputConnectionCompat.createWrapper(ic, editorInfo, callback)
    }

    fun setInputContentListener(inputContentListener: InputContentListener) {
        this.inputContentListener = inputContentListener
    }

    fun setSelectionChangedListener(selectionChangedListener: SelectionChangedListener) {
        this.selectionChangedListener = selectionChangedListener
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        selectionChangedListener?.invoke(selStart, selEnd)
    }

}