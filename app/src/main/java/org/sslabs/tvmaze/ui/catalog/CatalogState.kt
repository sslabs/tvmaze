package org.sslabs.tvmaze.ui.catalog

import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.util.Queue
import org.sslabs.tvmaze.util.StateMessage

data class CatalogState(
    val isLoading: Boolean = false,
    val isFavorites: Boolean = false,
    val catalog: List<Show> = listOf(),
    val page: Int = 0,
    val query: String = "",
    val isQueryExhausted: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)
