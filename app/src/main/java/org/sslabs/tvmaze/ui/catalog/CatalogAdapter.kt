package org.sslabs.tvmaze.ui.catalog

import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.request.RequestOptions
import org.sslabs.tvmaze.data.model.Show
import javax.inject.Inject

class CatalogAdapter @Inject constructor(
    private val catalogItemViewHolderFactory: CatalogItemViewHolderFactory,
    private val itemInteraction: CatalogItemViewHolder.Interaction,
    private val requestOptions: RequestOptions
) : RecyclerView.Adapter<CatalogItemViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Show>() {
        override fun areItemsTheSame(oldItem: Show, newItem: Show): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Show, newItem: Show): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            BlogRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(diffCallback).build()
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogItemViewHolder =
        catalogItemViewHolderFactory.create(parent, requestOptions, itemInteraction)

    override fun onBindViewHolder(holder: CatalogItemViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(showsList: List<Show>) {
        differ.submitList(showsList.toMutableList())
    }

    internal inner class BlogRecyclerChangeCallback(
        private val adapter: CatalogAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }
}
