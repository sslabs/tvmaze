package org.sslabs.tvmaze.ui.showdetails

import org.sslabs.tvmaze.data.model.Episode
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.util.Queue
import org.sslabs.tvmaze.util.StateMessage

data class EpisodesState(
    val isLoading: Boolean = false,
    val show: Show,
    val episodes: List<Episode>? = listOf(),
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)
