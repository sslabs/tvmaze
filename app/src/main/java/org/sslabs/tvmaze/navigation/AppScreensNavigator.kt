package org.sslabs.tvmaze.navigation

import androidx.navigation.NavController
import org.sslabs.tvmaze.data.model.Episode
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.ui.catalog.CatalogFragmentDirections
import org.sslabs.tvmaze.ui.showdetails.ShowFragmentDirections

class AppScreensNavigator(private val navController: NavController) : IScreensNavigator {

    override fun fromCatalogToShowDetails(show: Show) {
        navController.navigate(
            CatalogFragmentDirections.actionCatalogFragmentToShowFragment(
                show
            )
        )
    }

    override fun fromShowToEpisode(episode: Episode) {
        navController.navigate(
            ShowFragmentDirections.actionShowFragmentToEpisodeDetailsFragment(
                episode
            )
        )
    }
}
