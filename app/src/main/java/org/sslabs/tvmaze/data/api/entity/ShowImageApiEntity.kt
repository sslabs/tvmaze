package org.sslabs.tvmaze.data.api.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.sslabs.tvmaze.ShowApiConstants

data class ShowImageApiEntity(
    @Expose
    @SerializedName(ShowApiConstants.MEDIUM)
    val medium: String? = null,

    @Expose
    @SerializedName(ShowApiConstants.ORIGINAL)
    val original: String? = null
)
