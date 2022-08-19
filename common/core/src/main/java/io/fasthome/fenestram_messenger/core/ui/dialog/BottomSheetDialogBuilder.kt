package io.fasthome.fenestram_messenger.core.ui.dialog

import android.view.View
import android.widget.Button
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.fasthome.fenestram_messenger.core.R
import io.fasthome.fenestram_messenger.core.databinding.ViewBottomDialogContentBinding


class BottomSheetDialogBuilder(fragment: Fragment) {
    private val activity = fragment.requireActivity()
    private val dialog = BottomSheetDialog(activity, R.style.SheetDialog)
    private val binding = ViewBottomDialogContentBinding.inflate(activity.layoutInflater)

    val customViewContainer = binding.customViewContainer

    init {
        dialog.setContentView(binding.root)

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

    fun dismiss() {
        dialog.dismiss()
    }
}