package org.sslabs.tvmaze.navigation

import androidx.navigation.NavController
import org.sslabs.tvmaze.data.model.Episode
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.ui.SettingsFragmentDirections
import org.sslabs.tvmaze.ui.catalog.CatalogFragmentDirections
import org.sslabs.tvmaze.ui.showdetails.ShowFragmentDirections

class AppScreensNavigator(private val navController: NavController) : IScreensNavigator {

    override fun fromCatalogToSelf(displayFavorites: Boolean) {
        navController.navigate(
            CatalogFragmentDirections.actionCatalogFragmentSelf(displayFavorites)
        )
    }

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

    override fun toSettings() {
        navController.navigate(
            SettingsFragmentDirections.openSettingsFragment()
        )
    }
}
