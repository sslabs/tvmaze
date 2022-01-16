package org.sslabs.tvmaze.repository.shows

import kotlinx.coroutines.flow.Flow
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.util.DataState

interface IShowsRepository {
    fun getShows(page: Int): Flow<DataState<List<Show>>>
    fun getShowsFromCache(): Flow<DataState<List<Show>>>
}
