package org.sslabs.tvmaze.data.api

import org.sslabs.tvmaze.ApiConstants
import org.sslabs.tvmaze.data.api.entity.ShowApiEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface TvMazeApi {

    @GET(ApiConstants.SHOWS_PATH)
    suspend fun getShows(@Query(ApiConstants.SHOWS_PAGE_PARAM) page: Int): List<ShowApiEntity>
}
