package io.fasthome.fenestram_messenger.uikit.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import io.fasthome.fenestram_messenger.util.layoutInflater
import kotlin.reflect.KClass

abstract class AdapterDelegate<T : Any, B : ViewBinding> @PublishedApi internal constructor(
    val itemClazz: KClass<T>,
    val inflate: (layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) -> B,
    val initView: (binding: B) -> Unit,
    val bind: (item: T, binding: B) -> Unit,
)

inline fun <reified T : Any, B : ViewBinding> createAdapterDelegate(
    noinline inflate: (layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) -> B,
    noinline initView: (binding: B) -> Unit = {},
    noinline bind: (item: T, binding: B) -> Unit,
) = object : AdapterDelegate<T, B>(
    itemClazz = T::class,
    inflate = inflate,
    initView = initView,
    bind = bind,
) {}

abstract class PagerDelegateAdapter<T : Any>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val delegates: List<AdapterDelegate<out T, *>>,
) : PagingDataAdapter<T, PagerDelegateAdapter.VH<ViewBinding>>(diffCallback) {

    class VH<out B : ViewBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        val itemClazz = getItem(position)!!::class
        return delegates.indexOfFirst { it.itemClazz == itemClazz }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH<*> {
        val adapterDelegate = delegates[viewType] as AdapterDelegate<*, ViewBinding>
        val binding = adapterDelegate.inflate(
            parent.layoutInflater,
            parent,
            false
        )
        return VH(binding)
            .also { adapterDelegate.initView(binding) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: VH<*>, position: Int) {
        val itemViewType = getItemViewType(position)
        val adapterDelegate = delegates[itemViewType] as AdapterDelegate<T, ViewBinding>
        val item = getItem(position)!!
        adapterDelegate.bind(item, holder.binding)
    }
}