package org.sslabs.tvmaze.navigation

import androidx.navigation.NavController
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.ui.catalog.CatalogFragmentDirections

class AppScreensNavigator(private val navController: NavController) : IScreensNavigator {

    override fun fromCatalogToShowDetails(show: Show) {
        navController.navigate(
            CatalogFragmentDirections.actionCatalogFragmentToShowFragment(
                show
            )
        )
    }
}
