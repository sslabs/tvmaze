package org.sslabs.tvmaze.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.sslabs.tvmaze.util.StateMessage
import org.sslabs.tvmaze.util.UIComponentType
import org.sslabs.tvmaze.util.doesMessageAlreadyExistInQueue
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _state: MutableLiveData<MainState> = MutableLiveData(MainState())
    val state: LiveData<MainState>
        get() = _state

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
