package org.sslabs.tvmaze.ui.showdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.sslabs.tvmaze.repository.episode.IEpisodeRepository
import org.sslabs.tvmaze.repository.shows.IShowsRepository
import org.sslabs.tvmaze.util.StateMessage
import org.sslabs.tvmaze.util.UIComponentType
import org.sslabs.tvmaze.util.doesMessageAlreadyExistInQueue
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EpisodesViewModel @Inject constructor(
    private val showsRepository: IShowsRepository,
    private val episodeRepository: IEpisodeRepository,
    savedState: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<EpisodesState> = MutableStateFlow(
        EpisodesState(
            show = ShowFragmentArgs.fromSavedStateHandle(savedState).show
        )
    )

    val state: StateFlow<EpisodesState> = _state.asStateFlow()

    init {
        onTriggerEvent(EpisodesEvent.SearchEpisodes)
    }

    fun onTriggerEvent(event: EpisodesEvent) {
        when (event) {
            is EpisodesEvent.SearchEpisodes -> {
                searchEpisodes()
            }
            is EpisodesEvent.ToggleFavoriteShow -> {
                toggleShowFavorite()
            }
            is EpisodesEvent.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
            is EpisodesEvent.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
        }
    }

    private fun searchEpisodes() {
        val current = _state.value
        episodeRepository.getEpisodes(current.show.id).onEach { dataState ->
            _state.value = _state.value.copy(isLoading = dataState.isLoading)

            dataState.data?.let { episodes ->
                _state.value = _state.value.copy(episodes = episodes)
            }

            dataState.stateMessage?.let { stateMessage ->
                appendToMessageQueue(stateMessage)
            }
        }.launchIn(viewModelScope)
    }

    private fun toggleShowFavorite() {
        val current = _state.value
        showsRepository.toggleShowFavorite(current.show).onEach { dataState ->
            _state.value = _state.value.copy(isLoading = dataState.isLoading)

            dataState.data?.let { show ->
                _state.value = _state.value.copy(show = show)
            }

            dataState.stateMessage?.let { stateMessage ->
                appendToMessageQueue(stateMessage)
            }
        }.launchIn(viewModelScope)
    }

    private fun removeHeadFromQueue() {
        val current = _state.value
        try {
            val queue = current.queue
            queue.remove() // can throw exception if empty
            _state.value = current.copy(queue = queue)
        } catch (e: Exception) {
            Timber.i("removeHeadFromQueue: Nothing to remove from DialogQueue")
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage) {
        val current = _state.value
        val queue = current.queue
        if (!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)) {
            if (stateMessage.response.uiComponentType !is UIComponentType.None) {
                queue.add(stateMessage)
                _state.value = current.copy(queue = queue)
            }
        }
    }
}
