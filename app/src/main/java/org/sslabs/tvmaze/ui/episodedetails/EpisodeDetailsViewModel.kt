package org.sslabs.tvmaze.ui.episodedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.sslabs.tvmaze.data.model.Episode
import javax.inject.Inject

@HiltViewModel
class EpisodeDetailsViewModel @Inject constructor(
    savedState: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<Episode> = MutableStateFlow(
        EpisodeDetailsFragmentArgs.fromSavedStateHandle(savedState).episode
    )

    val state: StateFlow<Episode> = _state.asStateFlow()
}
