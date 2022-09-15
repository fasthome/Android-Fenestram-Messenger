package io.fasthome.fenestram_messenger.uikit.paging

import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import io.fasthome.fenestram_messenger.uikit.databinding.ItemErrorLoadStateBinding
import io.fasthome.fenestram_messenger.uikit.databinding.ItemProgressLoadStateBinding
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.layoutInflater
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText

class PagingLoadStateAdapter(
    private val error: PrintableText,
    private val retry: () -> Unit,
) : LoadStateAdapter<PagingLoadStateAdapter.ItemViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ERROR = 0
        private const val VIEW_TYPE_PROGRESS = 1
        private const val VIEW_TYPE_NOT_LOADING = 2
    }

    override fun onBindViewHolder(holder: ItemViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ItemViewHolder {
        return when (loadState) {
            LoadState.Loading -> ProgressViewHolder(
                binding = ItemProgressLoadStateBinding.inflate(parent.layoutInflater, parent, false)
            )
            is LoadState.Error -> ErrorViewHolder(
                binding = ItemErrorLoadStateBinding.inflate(parent.layoutInflater, parent, false),
                error = error,
                retry = retry,
            )
            is LoadState.NotLoading -> error("state LoadState.NotLoading shouldn't display as list item")
        }
    }

    abstract class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(loadState: LoadState)
    }

    private class ProgressViewHolder(binding: ItemProgressLoadStateBinding) :
        ItemViewHolder(binding.root) {

        override fun bind(loadState: LoadState) {}
    }

    private class ErrorViewHolder(
        private val binding: ItemErrorLoadStateBinding,
        private val error: PrintableText,
        private val retry: () -> Unit,
    ) : ItemViewHolder(binding.root) {

        override fun bind(loadState: LoadState) {
            binding.text.setPrintableText(error)
            binding.retryButton.onClick { retry() }
        }
    }

    override fun getStateViewType(loadState: LoadState): Int {
        return when (loadState) {
            is LoadState.NotLoading -> VIEW_TYPE_NOT_LOADING
            LoadState.Loading -> VIEW_TYPE_PROGRESS
            is LoadState.Error -> VIEW_TYPE_ERROR
        }
    }
}