package org.sslabs.tvmaze.ui.showdetails

import org.sslabs.tvmaze.util.StateMessage

sealed class EpisodesEvent {

    object SearchEpisodes : EpisodesEvent()

    object ToggleFavoriteShow : EpisodesEvent()

    object OnRemoveHeadFromQueue : EpisodesEvent()

    data class Error(val stateMessage: StateMessage): EpisodesEvent()
}
