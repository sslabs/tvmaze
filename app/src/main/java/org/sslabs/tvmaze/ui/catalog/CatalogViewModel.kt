package org.sslabs.tvmaze.ui.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _state: MutableStateFlow<CatalogState> = MutableStateFlow(CatalogState())
    val state: StateFlow<CatalogState> = _state.asStateFlow()

    init {
        val isLoadFavorites =
            CatalogFragmentArgs.fromSavedStateHandle(savedStateHandle).showFavorites
        val current = _state.value
        if (current.isFavorites != isLoadFavorites) {
            _state.value = current.copy(isFavorites = isLoadFavorites)
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
        showsRepository.getShowsFromCache().onEach { dataState ->
            _state.value = _state.value.copy(isLoading = dataState.isLoading)

            dataState.data?.let { list ->
                onUpdateLoadFromCache(list)
            }

            dataState.stateMessage?.let { stateMessage ->
                appendToMessageQueue(stateMessage)
            }
        }.launchIn(viewModelScope)
    }

    private fun index() {
        val current = _state.value
        showsRepository.getShows(current.page).onEach { dataState ->
            _state.value = _state.value.copy(isLoading = dataState.isLoading)

            dataState.data?.let { list ->
                _state.value = _state.value.copy(catalog = list)
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

    private fun search() {
        val current = _state.value
        showsRepository.searchShows(current.query).onEach { dataState ->
            _state.value = _state.value.copy(isLoading = dataState.isLoading)

            dataState.data?.let { list ->
                _state.value = _state.value.copy(catalog = list)
            }

            dataState.stateMessage?.let { stateMessage ->
                appendToMessageQueue(stateMessage)
            }
        }.launchIn(viewModelScope)
    }

    private fun nextPage() {
        incrementPageNumber()
        index()
    }

    private fun loadFavorites() {
        showsRepository.getFavorites().onEach { dataState ->
            _state.value = _state.value.copy(isLoading = dataState.isLoading)

            dataState.data?.let { list ->
                _state.value = _state.value.copy(catalog = list)
            }

            dataState.stateMessage?.let { stateMessage ->
                appendToMessageQueue(stateMessage)
            }
        }.launchIn(viewModelScope)
    }

    private fun incrementPageNumber() {
        val current = _state.value
        _state.value = current.copy(page = current.page + 1)
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

    private fun onUpdateQuery(query: String) {
        _state.value = _state.value.copy(query = query)
    }

    private fun onUpdateQueryExhausted(isExhausted: Boolean) {
        _state.value = _state.value.copy(isQueryExhausted = isExhausted)
    }

    private fun onUpdateLoadFromCache(list: List<Show>) {
        if (list.isEmpty()) {
            index()
        } else {
            val page = (list.last().id / ApiConstants.PAGE_SIZE)
            _state.value = _state.value.copy(catalog = list, page = page)
        }
    }
}
