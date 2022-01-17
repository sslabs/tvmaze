package org.sslabs.tvmaze.ui.episodedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.sslabs.tvmaze.data.model.Episode
import javax.inject.Inject

@HiltViewModel
class EpisodeDetailsViewModel @Inject constructor(
    savedState: SavedStateHandle
) : ViewModel() {

    private val _state: MutableLiveData<Episode> = MutableLiveData(
        EpisodeDetailsFragmentArgs.fromSavedStateHandle(savedState).episode
    )

    val state: LiveData<Episode>
        get() = _state
}
