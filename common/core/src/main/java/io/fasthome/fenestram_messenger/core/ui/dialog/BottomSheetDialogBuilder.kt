package io.fasthome.fenestram_messenger.core.ui.dialog

import android.content.res.ColorStateList
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.fasthome.fenestram_messenger.core.R
import io.fasthome.fenestram_messenger.uikit.databinding.ViewBottomDialogContentBinding
import io.fasthome.fenestram_messenger.uikit.theme.Theme


class BottomSheetDialogBuilder(val fragment: Fragment, val theme: Theme? = null) {
    private val activity = fragment.requireActivity()
    private val dialog = BottomSheetDialog(activity, R.style.SheetDialog)
    private val binding = ViewBottomDialogContentBinding.inflate(activity.layoutInflater)

    val customViewContainer = binding.customViewContainer

    init {
        dialog.setContentView(binding.root)
        theme?.let {
            binding.root.backgroundTintList = ColorStateList.valueOf(it.bg1Color())
        }

        /**
         * BottomSheetDialog запускается как отдельное окно, а не как часть фрагмента,
         * поэтому он остаётся видимым, если был открыт другой фрагмент.
         *
         * Чтобы избежать этого, dialog закрывается при уничтожении view фрагмента.
         *
         * Тут не получится использовать "fragment.viewLifecycleOwner.lifecycle.doOnDestroy {...}",
         * т.к. у fragment может не быть view.
         *
         * "FragmentManager.FragmentLifecycleCallbacks.onFragmentViewDestroyed" вызывается всегда,
         * независимо от наличия view у фрагмента.
         */
        val fragmentManager = fragment.parentFragmentManager
        fragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                    if (f === fragment) {
                        fragmentManager.unregisterFragmentLifecycleCallbacks(this)
                        dismiss()
                    }
                }
            }, false
        )
    }

    fun addCustomView(customView: View): BottomSheetDialogBuilder {
        binding.customViewContainer.addView(customView)
        return this
    }

    fun setCancelable(cancellable: Boolean): BottomSheetDialogBuilder {
        dialog.setCancelable(cancellable)
        return this
    }

    fun expanded(): BottomSheetDialogBuilder {
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return this
    }

    fun build(): BottomSheetDialog {
        with(binding) {
            customViewContainer.isVisible = customViewContainer.childCount != 0
        }
        return dialog
    }

    fun setBackground(colorRes: Int){
        binding.root.backgroundTintList = ColorStateList.valueOf(colorRes)
        binding.customViewContainer.background = colorRes.toDrawable()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}