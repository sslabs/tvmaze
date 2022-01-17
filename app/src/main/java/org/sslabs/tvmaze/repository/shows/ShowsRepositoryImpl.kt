package org.sslabs.tvmaze.repository.shows

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.sslabs.tvmaze.data.api.TvMazeApi
import org.sslabs.tvmaze.data.api.handleUseCaseException
import org.sslabs.tvmaze.data.api.mapper.ShowApiMapper
import org.sslabs.tvmaze.data.local.db.dao.ShowDao
import org.sslabs.tvmaze.data.local.db.mapper.ShowCacheMapper
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.util.DataState
import org.sslabs.tvmaze.util.ErrorHandling
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowsRepositoryImpl @Inject constructor(
    private val tvMazeApi: TvMazeApi,
    private val showDao: ShowDao,
    private val showCacheMapper: ShowCacheMapper,
    private val showApiMapper: ShowApiMapper
) : IShowsRepository {

    override fun getShows(page: Int): Flow<DataState<List<Show>>> = flow {
        emit(DataState.loading())
        val apiShows = tvMazeApi.getShows(page)
        val shows = showApiMapper.mapFromEntityList(apiShows)
        var cacheShows = showCacheMapper.mapToEntityList(shows)
        cacheShows.forEach { showDao.insert(it) }
        cacheShows = showDao.getAll()
        emit(DataState.data(response = null, data = showCacheMapper.mapFromEntityList(cacheShows)))
    }.catch { e ->
        when (e) {
            is HttpException -> {
                if (e.code() == 404 && e.response()?.body() == null) {
                    emit(handleUseCaseException(RuntimeException(ErrorHandling.INVALID_PAGE)))
                } else {
                    emit(handleUseCaseException(e))
                }
            }
            else -> emit(handleUseCaseException(e))
        }
    }

    override fun getShowsFromCache(): Flow<DataState<List<Show>>> = flow {
        emit(DataState.loading())
        val cacheShows = showDao.getAll()
        emit(DataState.data(response = null, data = showCacheMapper.mapFromEntityList(cacheShows)))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }

    override fun searchShows(query: String) = flow {
        emit(DataState.loading())
        val apiShows = tvMazeApi.searchShows(query).map { it.show!! }
        val shows = showApiMapper.mapFromEntityList(apiShows)
        var cacheShows = showCacheMapper.mapToEntityList(shows)
        cacheShows.forEach { showDao.insert(it) }
        val apiIds = apiShows.map { it.id }
        cacheShows = showDao.queryIdIn(apiIds)
        emit(DataState.data(response = null, data = showCacheMapper.mapFromEntityList(cacheShows)))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }

    override fun toggleShowFavorite(show: Show) = flow {
        val toggle = show.copy(favorite = show.favorite?.not())
        var cacheToggle = showCacheMapper.toEntity(toggle)
        showDao.update(cacheToggle)
        cacheToggle = showDao.getById(show.id)
        emit(DataState.data(response = null, data = showCacheMapper.fromEntity(cacheToggle)))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }

    override fun getFavorites() = flow {
        emit(DataState.loading())
        val favorites = showDao.queryFavorites(isFavorite = true)
        emit(DataState.data(response = null, data = showCacheMapper.mapFromEntityList(favorites)))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}
