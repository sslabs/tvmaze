package org.sslabs.tvmaze.repository.episode

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.sslabs.tvmaze.data.api.TvMazeApi
import org.sslabs.tvmaze.data.api.handleUseCaseException
import org.sslabs.tvmaze.data.api.mapper.EpisodeApiMapper
import org.sslabs.tvmaze.data.local.db.dao.EpisodeDao
import org.sslabs.tvmaze.data.local.db.mapper.EpisodeCacheMapper
import org.sslabs.tvmaze.data.model.Episode
import org.sslabs.tvmaze.util.DataState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodeRepositoryImpl @Inject constructor(
    private val tvMazeApi: TvMazeApi,
    private val episodeDao: EpisodeDao,
    private val episodeCacheMapper: EpisodeCacheMapper,
    private val episodeApiMapper: EpisodeApiMapper
) : IEpisodeRepository {

    override fun getEpisodes(showId: Int): Flow<DataState<List<Episode>>> = flow {
        emit(DataState.loading())
        val apiEpisodes = tvMazeApi.getEpisodes(showId)
        val episodes = episodeApiMapper
            .forShow(showId)
            .mapFromEntityList(apiEpisodes)
        var cacheEpisodes = episodeCacheMapper.mapToEntityList(episodes)
        cacheEpisodes.forEach { episodeDao.insert(it) }
        cacheEpisodes = episodeDao.searchShowEpisodes(showId)
        emit(
            DataState.data(
                data = episodeCacheMapper.mapFromEntityList(cacheEpisodes),
                response = null
            )
        )
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}
