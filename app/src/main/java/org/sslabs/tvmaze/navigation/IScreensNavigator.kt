package org.sslabs.tvmaze.navigation

import org.sslabs.tvmaze.data.model.Episode
import org.sslabs.tvmaze.data.model.Show

interface IScreensNavigator {
    fun fromCatalogToSelf(displayFavorites: Boolean)

    fun fromCatalogToShowDetails(show: Show)

    fun fromShowToEpisode(episode: Episode)

    fun toSettings()
}
