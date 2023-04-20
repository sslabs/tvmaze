package org.sslabs.tvmaze.ui.catalog

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.sslabs.tvmaze.ApiConstants
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.repository.shows.IShowsRepository
import org.sslabs.tvmaze.util.ErrorHandling
import org.sslabs.tvmaze.util.StateMessage
import org.sslabs.tvmaze.util.UIComponentType
import org.sslabs.tvmaze.util.doesMessageAlreadyExistInQueue
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val showsRepository: IShowsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableLiveData<CatalogState> = MutableLiveData(CatalogState())
    val state: LiveData<CatalogState>
        get() = _state

    init {
        val isLoadFavorites =
            CatalogFragmentArgs.fromSavedStateHandle(savedStateHandle).showFavorites
        _state.value?.let { state ->
            if (state.isFavorites != isLoadFavorites) {
                _state.value = state.copy(isFavorites = isLoadFavorites)
            }
        }

        if (isLoadFavorites) {
            onTriggerEvent(CatalogEvent.Favorites)
        } else {
            onTriggerEvent(CatalogEvent.Index)
        }
    }

    fun onTriggerEvent(event: CatalogEvent) {
        when (event) {
            is CatalogEvent.Index -> {
                firstLoad()
            }
            is CatalogEvent.NewSearch -> {
                search()
            }
            is CatalogEvent.NextPage -> {
                nextPage()
            }
            is CatalogEvent.Favorites -> {
                loadFavorites()
            }
            is CatalogEvent.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
            is CatalogEvent.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is CatalogEvent.UpdateQuery -> {
                onUpdateQuery(event.query)
            }
        }
    }

    private fun firstLoad() {
        _state.value?.let { state ->
            showsRepository.getShowsFromCache().onEach { dataState ->
                this._state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    onUpdateLoadFromCache(list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun index() {
        _state.value?.let { state ->
            showsRepository.getShows(state.page).onEach { dataState ->
                this._state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this._state.value = state.copy(catalog = list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if (stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true) {
                        onUpdateQueryExhausted(true)
                    } else {
                        appendToMessageQueue(stateMessage)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun search() {
        _state.value?.let { state ->
            showsRepository.searchShows(state.query).onEach { dataState ->
                this._state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this._state.value = state.copy(catalog = list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun nextPage() {
        incrementPageNumber()
        index()
    }

    private fun loadFavorites() {
        _state.value?.let { state ->
            showsRepository.getFavorites().onEach { dataState ->
                this._state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this._state.value = state.copy(catalog = list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun incrementPageNumber() {
        _state.value?.let { state ->
            this._state.value = state.copy(page = state.page + 1)
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

    private fun onUpdateQuery(query: String) {
        _state.value = state.value?.copy(query = query)
    }

    private fun onUpdateQueryExhausted(isExhausted: Boolean) {
        state.value?.let { state ->
            this._state.value = state.copy(isQueryExhausted = isExhausted)
        }
    }

    private fun onUpdateLoadFromCache(list: List<Show>) {
        _state.value?.let { state ->
            if (list.isEmpty()) {
                index()
            } else {
                val page = (list.last().id / ApiConstants.PAGE_SIZE)
                this._state.value = state.copy(catalog = list, page = page)
            }
        }
    }
}
