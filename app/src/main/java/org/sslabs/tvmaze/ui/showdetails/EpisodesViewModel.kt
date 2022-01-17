package org.sslabs.tvmaze.ui.showdetails

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _state: MutableLiveData<EpisodesState> = MutableLiveData(
        EpisodesState(
            show = ShowFragmentArgs.fromSavedStateHandle(savedState).show
        )
    )

    val state: LiveData<EpisodesState>
        get() = _state

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
        _state.value?.let { state ->
            episodeRepository.getEpisodes(state.show.id).onEach { dataState ->
                this._state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { episodes ->
                    this._state.value = state.copy(episodes = episodes)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun toggleShowFavorite() {
        _state.value?.let { state ->
            showsRepository.toggleShowFavorite(state.show).onEach { dataState ->
                _state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { show ->
                    _state.value = state.copy(show = show)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun removeHeadFromQueue() {
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this._state.value = state.copy(queue = queue)
            } catch (e: Exception) {
                Timber.i("removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage) {
        state.value?.let { state ->
            val queue = state.queue
            if (!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)) {
                if (stateMessage.response.uiComponentType !is UIComponentType.None) {
                    queue.add(stateMessage)
                    this._state.value = state.copy(queue = queue)
                }
            }
        }
    }
}
