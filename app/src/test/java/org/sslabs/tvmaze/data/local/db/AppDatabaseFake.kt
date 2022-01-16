package org.sslabs.tvmaze.data.local.db

import org.sslabs.tvmaze.data.local.db.entity.ShowCacheEntity

class AppDatabaseFake {
    val shows = mutableListOf<ShowCacheEntity>()
}