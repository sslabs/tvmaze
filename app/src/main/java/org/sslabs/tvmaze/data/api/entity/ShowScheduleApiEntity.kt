package org.sslabs.tvmaze.data.api.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.sslabs.tvmaze.ShowApiConstants

data class ShowScheduleApiEntity(
    @Expose
    @SerializedName(ShowApiConstants.TIME)
    val time: String? = null,

    @Expose
    @SerializedName(ShowApiConstants.DAYS)
    val days: List<String>? = null
)
