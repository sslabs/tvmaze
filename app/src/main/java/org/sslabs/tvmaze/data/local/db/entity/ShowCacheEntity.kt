package org.sslabs.tvmaze.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.sslabs.tvmaze.ShowDbConstants

@Entity(tableName = ShowDbConstants.TABLE_NAME)
data class ShowCacheEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = ShowDbConstants.ID)
    var id: Int,

    @ColumnInfo(name = ShowDbConstants.NAME)
    var name: String?,

    @ColumnInfo(name = ShowDbConstants.IMAGE)
    var image: String?,

    @ColumnInfo(name = ShowDbConstants.TIME)
    var time: String?,

    @ColumnInfo(name = ShowDbConstants.DAYS)
    var days: String?,

    @ColumnInfo(name = ShowDbConstants.GENRES)
    var genres: String?,

    @ColumnInfo(name = ShowDbConstants.SUMMARY)
    var summary: String?
)
