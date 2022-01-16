package org.sslabs.tvmaze.data.api.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.sslabs.tvmaze.ShowSearchResultApiConstants

data class ShowSearchResultApiEntity(
    @Expose
    @SerializedName(ShowSearchResultApiConstants.SCORE)
    val score: Float? = null,

    @Expose
    @SerializedName(ShowSearchResultApiConstants.SHOW)
    val show: ShowApiEntity? = null,
)