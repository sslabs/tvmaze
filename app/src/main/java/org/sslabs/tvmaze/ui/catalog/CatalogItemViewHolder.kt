package org.sslabs.tvmaze.ui.catalog

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.databinding.ItemShowBinding

class CatalogItemViewHolder(
    private val itemBinding: ItemShowBinding,
    private val requestOptions: RequestOptions,
    private val interaction: Interaction
) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(show: Show) {
        itemBinding.apply {
            root.setOnClickListener { interaction.onCatalogItemSelected(adapterPosition, show) }
            itemShowTitle.text = show.name
            Glide.with(this.root)
                .setDefaultRequestOptions(requestOptions)
                .load(show.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(this.itemShowPoster)
        }
    }

    interface Interaction {
        fun onCatalogItemSelected(position: Int, item: Show)
    }
}
