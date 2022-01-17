package org.sslabs.tvmaze.data.local.db.dao

import androidx.room.*
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

    @Query("""
        SELECT *
        FROM ${ShowDbConstants.TABLE_NAME}
        WHERE ${ShowDbConstants.ID} = :id""")
    suspend fun getById(id: Int): ShowCacheEntity

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(show: ShowCacheEntity)

    @Query("""
        SELECT *
        FROM ${ShowDbConstants.TABLE_NAME}
        WHERE ${ShowDbConstants.FAVORITE} = :isFavorite
        ORDER BY ${ShowDbConstants.NAME} ASC
    """)
    suspend fun queryFavorites(isFavorite: Boolean = true): List<ShowCacheEntity>

    @Query("""
        SELECT *
        FROM ${ShowDbConstants.TABLE_NAME} 
        WHERE ${ShowDbConstants.ID} IN (:ids)
        ORDER BY ${ShowDbConstants.ID} ASC""")
    suspend fun queryIdIn(ids: List<Int>): List<ShowCacheEntity>
}
