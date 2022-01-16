package org.sslabs.tvmaze.data.api

import org.sslabs.tvmaze.ApiConstants
import org.sslabs.tvmaze.data.api.entity.EpisodeApiEntity
import org.sslabs.tvmaze.data.api.entity.ShowApiEntity
import org.sslabs.tvmaze.data.api.entity.ShowSearchResultApiEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvMazeApi {

    @GET(ApiConstants.SHOWS_INDEX_PATH)
    suspend fun getShows(@Query(ApiConstants.SHOWS_PAGE_PARAM) page: Int): List<ShowApiEntity>

    @GET(ApiConstants.SHOWS_SEARCH_PATH)
    suspend fun searchShows(
        @Query(ApiConstants.SHOWS_SEARCH_QUERY_PARAM) query: String
    ): List<ShowSearchResultApiEntity>

    @GET(ApiConstants.EPISODE_SEARCH_PATH)
    suspend fun getEpisodes(@Path(ApiConstants.EPISODE_SHOW_ID) showId: Int): List<EpisodeApiEntity>
}
