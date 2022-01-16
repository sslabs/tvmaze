package org.sslabs.tvmaze.ui.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import org.sslabs.tvmaze.databinding.ItemShowBinding
import javax.inject.Inject

class CatalogItemViewHolderFactory @Inject constructor() {

    fun create(
        parent: ViewGroup,
        requestOptions: RequestOptions,
        interaction: CatalogItemViewHolder.Interaction)
    = CatalogItemViewHolder(
        ItemShowBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        requestOptions,
        interaction
    )
}