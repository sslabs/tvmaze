package org.sslabs.tvmaze.di.fragment

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import org.sslabs.tvmaze.ui.catalog.CatalogItemViewHolder

@Module
@InstallIn(FragmentComponent::class)
object CatalogFragmentModule {

    @Provides
    fun provideCatalogItemInteraction(fragment: Fragment): CatalogItemViewHolder.Interaction =
        fragment as CatalogItemViewHolder.Interaction
}
