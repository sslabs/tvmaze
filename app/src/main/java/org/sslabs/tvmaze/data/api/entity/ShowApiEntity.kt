package org.sslabs.tvmaze.data.api.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.sslabs.tvmaze.ShowApiConstants

data class ShowApiEntity(
    @Expose
    @SerializedName(ShowApiConstants.ID)
    val id: Int,

    @Expose
    @SerializedName(ShowApiConstants.NAME)
    val name: String? = null,

    @Expose
    @SerializedName(ShowApiConstants.IMAGE)
    val image: ImageApiEntity? = null,

    @Expose
    @SerializedName(ShowApiConstants.SCHEDULE)
    val schedule: ShowScheduleApiEntity? = null,

    @Expose
    @SerializedName(ShowApiConstants.GENRES)
    val genres: List<String>? = null,

    @Expose
    @SerializedName(ShowApiConstants.SUMMARY)
    val summary: String? = null
)
