package org.sslabs.tvmaze

object ApiConstants {
    const val BASE_URL = "https://api.tvmaze.com/"
    const val SHOWS_PATH = "shows"
    const val SHOWS_PAGE_PARAM = "page"
    const val PAGE_SIZE = 250 // Per API documentation
}

object ShowApiConstants {
    const val ID = "id"
    const val NAME = "name"
    const val IMAGE = "image"
    const val MEDIUM = "MEDIUM"
    const val ORIGINAL = "original"
    const val SCHEDULE = "schedule"
    const val TIME = "time"
    const val DAYS = "days"
    const val GENRES = "genres"
    const val SUMMARY = "summary"
}

object ShowDbConstants {
    const val TABLE_NAME = "show"
    const val ID = "id"
    const val NAME = "name"
    const val IMAGE = "image"
    const val TIME = "time"
    const val DAYS = "days"
    const val GENRES = "genres"
    const val SUMMARY = "summary"
}