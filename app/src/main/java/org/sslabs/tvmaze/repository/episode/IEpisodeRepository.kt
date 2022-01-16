package org.sslabs.tvmaze.repository.episode

import kotlinx.coroutines.flow.Flow
import org.sslabs.tvmaze.data.model.Episode
import org.sslabs.tvmaze.util.DataState

interface IEpisodeRepository {
    fun getEpisodes(showId: Int): Flow<DataState<List<Episode>>>
}
