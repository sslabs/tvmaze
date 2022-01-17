package org.sslabs.tvmaze.ui

import org.sslabs.tvmaze.util.Queue
import org.sslabs.tvmaze.util.StateMessage

data class MainState(
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)
