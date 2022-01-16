package org.sslabs.tvmaze.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.sslabs.tvmaze.EpisodeDbConstants

@Entity(tableName = EpisodeDbConstants.TABLE_NAME)
data class EpisodeCacheEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = EpisodeDbConstants.ID)
    var id: Int,

    @ColumnInfo(name = EpisodeDbConstants.SHOW_ID)
    var showId: Int,

    @ColumnInfo(name = EpisodeDbConstants.NAME)
    var name: String?,

    @ColumnInfo(name = EpisodeDbConstants.NUMBER)
    var number: Int?,

    @ColumnInfo(name = EpisodeDbConstants.SEASON)
    var season: Int?,

    @ColumnInfo(name = EpisodeDbConstants.SUMMARY)
    var summary: String?,

    @ColumnInfo(name = EpisodeDbConstants.IMAGE)
    var image: String?,
)
