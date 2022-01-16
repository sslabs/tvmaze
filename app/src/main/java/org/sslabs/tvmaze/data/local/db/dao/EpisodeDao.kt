package org.sslabs.tvmaze.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.sslabs.tvmaze.EpisodeDbConstants
import org.sslabs.tvmaze.data.local.db.entity.EpisodeCacheEntity

@Dao
interface EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(episode: EpisodeCacheEntity)

    @Query("""
        SELECT *
        FROM ${EpisodeDbConstants.TABLE_NAME}
        WHERE ${EpisodeDbConstants.SHOW_ID} = :showId
        ORDER BY ${EpisodeDbConstants.SEASON}, ${EpisodeDbConstants.NUMBER} ASC""")
    suspend fun searchShowEpisodes(showId: Int): List<EpisodeCacheEntity>
}
