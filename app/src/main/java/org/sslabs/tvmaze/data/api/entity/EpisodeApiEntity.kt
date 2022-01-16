package org.sslabs.tvmaze.data.api.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.sslabs.tvmaze.EpisodeApiConstants

data class EpisodeApiEntity(
    @Expose
    @SerializedName(EpisodeApiConstants.ID)
    val id: Int,

    @Expose
    @SerializedName(EpisodeApiConstants.NAME)
    val name: String? = null,

    @Expose
    @SerializedName(EpisodeApiConstants.NUMBER)
    val number: Int? = null,

    @Expose
    @SerializedName(EpisodeApiConstants.SEASON)
    val season: Int? = null,

    @Expose
    @SerializedName(EpisodeApiConstants.SUMMARY)
    val summary: String? = null,

    @Expose
    @SerializedName(EpisodeApiConstants.IMAGE)
    val image: ImageApiEntity? = null
)
