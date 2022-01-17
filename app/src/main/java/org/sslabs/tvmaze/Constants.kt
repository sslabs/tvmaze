package org.sslabs.tvmaze

object ApiConstants {
    const val BASE_URL = "https://api.tvmaze.com/"

    const val SHOWS_INDEX_PATH = "shows"
    const val SHOWS_SEARCH_PATH = "search/shows"
    const val SHOWS_PAGE_PARAM = "page"
    const val SHOWS_SEARCH_QUERY_PARAM = "q"
    const val PAGE_SIZE = 250 // Per API documentation

    const val EPISODE_SHOW_ID = "showId"
    const val EPISODE_SEARCH_PATH = "shows/{${EPISODE_SHOW_ID}}/episodes"
}

object ShowApiConstants {
    const val ID = "id"
    const val NAME = "name"
    const val IMAGE = "image"
    const val SCHEDULE = "schedule"
    const val TIME = "time"
    const val DAYS = "days"
    const val GENRES = "genres"
    const val SUMMARY = "summary"
}

object ImageApiConstants {
    const val MEDIUM = "medium"
    const val ORIGINAL = "original"
}

object EpisodeApiConstants {
    const val ID = "id"
    const val NAME = "name"
    const val NUMBER = "number"
    const val SEASON = "season"
    const val SUMMARY = "summary"
    const val IMAGE = "image"
}

object ShowSearchResultApiConstants {
    const val SCORE = "score"
    const val SHOW = "show"
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
    const val FAVORITE = "favorite"
}

object EpisodeDbConstants {
    const val TABLE_NAME = "episode"
    const val ID = "id"
    const val SHOW_ID = "show_id"
    const val NAME = "name"
    const val NUMBER = "number"
    const val SEASON = "season"
    const val SUMMARY = "summary"
    const val IMAGE = "image"
}

object SettingsKeys {
    const val AUTH_SETTING_KEY = "authentication"
}
