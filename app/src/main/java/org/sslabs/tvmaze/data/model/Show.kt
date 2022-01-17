package org.sslabs.tvmaze.data.model

import java.io.Serializable

data class Show(
    val id: Int,
    val name: String?,
    val image: String?,
    val time: String?,
    val days: List<String>?,
    val genres: List<String>?,
    val summary: String?,
    val favorite: Boolean?
) : Serializable
