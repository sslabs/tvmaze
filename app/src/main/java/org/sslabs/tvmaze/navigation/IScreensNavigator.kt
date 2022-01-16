package org.sslabs.tvmaze.navigation

import org.sslabs.tvmaze.data.model.Show

interface IScreensNavigator {
    fun fromCatalogToShowDetails(show: Show)
}
