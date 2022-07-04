package io.fasthome.fenestram_messenger.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.AdapterDelegateViewBindingViewHolder
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

object AdapterUtil {

    fun <T : Any> diffUtilItemCallbackEquals() = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
    }

    fun <T : Any, K> diffUtilItemCallbackEquals(
        keyExtractor: (T) -> K,
        getChangePayload: (oldItem: T, newItem: T) -> Any? = { _, _ -> null },
    ) = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) =
            keyExtractor(oldItem) == keyExtractor(newItem)

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem

        override fun getChangePayload(oldItem: T, newItem: T): Any? = getChangePayload(oldItem, newItem)
    }

    inline fun <T : Any, reified T1 : T> diffUtilItemCallbackEquals(
        crossinline keyExtractorT1: (T1) -> Any?,
    ) = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = when {
            (oldItem is T1 && newItem is T1) -> keyExtractorT1(oldItem) == keyExtractorT1(newItem)
            else -> oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
    }

    inline fun <T : Any, reified T1 : T, reified T2 : T> diffUtilItemCallbackEquals(
        crossinline keyExtractorT1: (T1) -> Any?,
        crossinline keyExtractorT2: (T2) -> Any?,
    ) = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = when {
            (oldItem is T1 && newItem is T1) -> keyExtractorT1(oldItem) == keyExtractorT1(newItem)
            (oldItem is T2 && newItem is T2) -> keyExtractorT2(oldItem) == keyExtractorT2(newItem)
            else -> oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
    }

    fun <T> adapterDelegatesManager(vararg delegates: AdapterDelegate<out List<T>>): AdapterDelegatesManager<List<T>> =
        AdapterDelegatesManager<List<T>>().apply {
            delegates.forEach { addDelegate(it as AdapterDelegate<List<T>>) }
        }
}

inline fun <reified T, B : ViewBinding> adapterDelegateViewBinding(
    noinline viewBinding: (LayoutInflater, ViewGroup, Boolean) -> B,
    noinline block: AdapterDelegateViewBindingViewHolder<T, B>.() -> Unit,
): AdapterDelegate<List<T>> = adapterDelegateViewBinding(
    viewBinding = { layoutInflater, parent -> viewBinding(layoutInflater, parent, false) },
    block = block,
)

fun <B : ViewBinding> AdapterDelegateViewBindingViewHolder<*, B>.bindWithBinding(
    render: B.(payload: List<Any>?) -> Unit,
) {
    with(binding) {
        bind { render(it) }
    }
}