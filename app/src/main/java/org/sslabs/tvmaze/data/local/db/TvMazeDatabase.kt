package org.sslabs.tvmaze.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.sslabs.tvmaze.data.local.db.dao.EpisodeDao
import org.sslabs.tvmaze.data.local.db.dao.ShowDao
import org.sslabs.tvmaze.data.local.db.entity.EpisodeCacheEntity
import org.sslabs.tvmaze.data.local.db.entity.ShowCacheEntity

@Database(
    entities = [
        ShowCacheEntity::class,
        EpisodeCacheEntity::class
    ],
    version = 1
)
abstract class TvMazeDatabase : RoomDatabase() {
    abstract fun showDao(): ShowDao
    abstract fun episodeDao(): EpisodeDao

    companion object {
        const val DATABASE_NAME: String = "tvmaze.db"
    }
}
