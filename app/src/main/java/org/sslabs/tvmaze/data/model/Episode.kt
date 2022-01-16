package org.sslabs.tvmaze.data.model

import java.io.Serializable

data class Episode(
    val id: Int,
    val showId: Int,
    val name: String? = null,
    val number: Int? = null,
    val season: Int? = null,
    val summary: String? = null,
    val image: String? = null
) : Serializable
