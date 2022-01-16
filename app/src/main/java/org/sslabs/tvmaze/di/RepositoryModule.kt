package org.sslabs.tvmaze.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.sslabs.tvmaze.data.api.TvMazeApi
import org.sslabs.tvmaze.data.api.mapper.EpisodeApiMapper
import org.sslabs.tvmaze.data.api.mapper.ShowApiMapper
import org.sslabs.tvmaze.data.local.db.dao.EpisodeDao
import org.sslabs.tvmaze.data.local.db.dao.ShowDao
import org.sslabs.tvmaze.data.local.db.mapper.EpisodeCacheMapper
import org.sslabs.tvmaze.data.local.db.mapper.ShowCacheMapper
import org.sslabs.tvmaze.repository.episode.EpisodeRepositoryImpl
import org.sslabs.tvmaze.repository.episode.IEpisodeRepository
import org.sslabs.tvmaze.repository.shows.IShowsRepository
import org.sslabs.tvmaze.repository.shows.ShowsRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideShowsRepository(
        tvMazeApi: TvMazeApi,
        showDao: ShowDao,
        showCacheMapper: ShowCacheMapper,
        showApiMapper: ShowApiMapper
    ): IShowsRepository = ShowsRepositoryImpl(
        tvMazeApi,
        showDao,
        showCacheMapper,
        showApiMapper
    )

    @Singleton
    @Provides
    fun provideEpisodeRepository(
        tvMazeApi: TvMazeApi,
        episodeDao: EpisodeDao,
        episodeCacheMapper: EpisodeCacheMapper,
        episodeApiMapper: EpisodeApiMapper
    ): IEpisodeRepository = EpisodeRepositoryImpl(
        tvMazeApi,
        episodeDao,
        episodeCacheMapper,
        episodeApiMapper
    )
}
