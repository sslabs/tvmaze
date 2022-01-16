package org.sslabs.tvmaze.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.sslabs.tvmaze.ShowDbConstants
import org.sslabs.tvmaze.data.local.db.entity.ShowCacheEntity

@Dao
interface ShowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(show: ShowCacheEntity)

    @Query("DELETE FROM ${ShowDbConstants.TABLE_NAME}")
    suspend fun deleteAll()

    @Query("SELECT * FROM ${ShowDbConstants.TABLE_NAME} ORDER BY ${ShowDbConstants.ID} ASC")
    suspend fun getAll(): List<ShowCacheEntity>
}
