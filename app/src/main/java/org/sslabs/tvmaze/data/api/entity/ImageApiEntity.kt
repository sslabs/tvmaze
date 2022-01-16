package org.sslabs.tvmaze.data.api.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.sslabs.tvmaze.ImageApiConstants

data class ImageApiEntity(
    @Expose
    @SerializedName(ImageApiConstants.MEDIUM)
    val medium: String? = null,

    @Expose
    @SerializedName(ImageApiConstants.ORIGINAL)
    val original: String? = null
)
