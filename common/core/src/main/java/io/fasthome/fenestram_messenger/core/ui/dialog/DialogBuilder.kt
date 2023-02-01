package io.fasthome.fenestram_messenger.core.ui.dialog

import android.app.Dialog
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import io.fasthome.fenestram_messenger.core.R
import io.fasthome.fenestram_messenger.uikit.databinding.ViewDialogContentBinding


class DialogBuilder(fragment: Fragment) {

    private val activity = fragment.requireActivity()
    private val dialog = Dialog(activity, R.style.SheetDialog)
    private val binding = ViewDialogContentBinding.inflate(activity.layoutInflater)
    val customViewContainer = binding.customViewContainer

    init {
        dialog.setContentView(binding.root)

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

    fun addCustomView(customView: View): DialogBuilder {
        binding.customViewContainer.addView(customView)
        return this
    }

    fun setCancelable(cancellable: Boolean): DialogBuilder {
        dialog.setCancelable(cancellable)
        return this
    }

    fun build(): Dialog {
        with(binding) {
            customViewContainer.isVisible = customViewContainer.childCount != 0
        }
        return dialog
    }

    fun dismiss() {
        dialog.dismiss()
    }
}