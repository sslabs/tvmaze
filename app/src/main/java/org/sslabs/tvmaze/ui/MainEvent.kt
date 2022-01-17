package org.sslabs.tvmaze.ui

import org.sslabs.tvmaze.util.StateMessage

sealed class MainEvent {

    object OnRemoveHeadFromQueue : MainEvent()

    data class Error(val stateMessage: StateMessage) : MainEvent()
}
