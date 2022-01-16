package org.sslabs.tvmaze.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import org.sslabs.tvmaze.data.local.db.dao.ShowDao
import org.sslabs.tvmaze.data.local.db.entity.ShowCacheEntity

@Database(
    entities = [ShowCacheEntity::class],
    version = 1
)
abstract class TvMazeDatabase : RoomDatabase() {
    abstract fun showDao(): ShowDao

    companion object {
        const val DATABASE_NAME: String = "tvmaze.db"
    }
}
