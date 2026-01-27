package org.sslabs.tvmaze.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.sslabs.tvmaze.util.StateMessage
import org.sslabs.tvmaze.util.UIComponentType
import org.sslabs.tvmaze.util.doesMessageAlreadyExistInQueue
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _state: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    fun onTriggerEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
            is MainEvent.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
        }
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
