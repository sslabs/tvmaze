package org.sslabs.tvmaze.data.local.db.dao

import org.sslabs.tvmaze.data.local.db.AppDatabaseFake
import org.sslabs.tvmaze.data.local.db.entity.ShowCacheEntity
import java.lang.RuntimeException

class ShowDaoFake(
    private val db: AppDatabaseFake
): ShowDao {
    var raiseException: Boolean = false

    override suspend fun insert(show: ShowCacheEntity) {
        if (raiseException) throw RuntimeException()
        db.shows.removeIf {
            it.id == show.id
        }
        db.shows.add(show)
    }

    override suspend fun deleteAll() {
        if (raiseException) throw RuntimeException()
        db.shows.clear()
    }

    override suspend fun getAll(): List<ShowCacheEntity> {
        if (raiseException) throw RuntimeException()
        return db.shows
    }

    override suspend fun getById(id: Int): ShowCacheEntity {
        if (raiseException) throw RuntimeException()
        return db.shows.first { it.id == id }
    }

    override suspend fun update(show: ShowCacheEntity) {
        if (raiseException) throw RuntimeException()
        db.shows.removeIf { it.id == show.id }
        db.shows.add(show)
    }

    override suspend fun queryFavorites(isFavorite: Boolean): List<ShowCacheEntity> {
        if (raiseException) throw RuntimeException()
        return db.shows.filter { it.favorite == isFavorite }
    }

    override suspend fun queryIdIn(ids: List<Int>): List<ShowCacheEntity> {
        if (raiseException) throw RuntimeException()
        return db.shows.filter { ids.contains(it.id) }
    }
}
