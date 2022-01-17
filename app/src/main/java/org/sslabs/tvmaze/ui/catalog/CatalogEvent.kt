package org.sslabs.tvmaze.ui.catalog

import org.sslabs.tvmaze.util.StateMessage

sealed class CatalogEvent {

    object Index : CatalogEvent()

    object NewSearch : CatalogEvent()

    object NextPage : CatalogEvent()

    object Favorites : CatalogEvent()

    data class UpdateQuery(val query: String): CatalogEvent()

    object OnRemoveHeadFromQueue : CatalogEvent()

    data class Error(val stateMessage: StateMessage): CatalogEvent()
}
