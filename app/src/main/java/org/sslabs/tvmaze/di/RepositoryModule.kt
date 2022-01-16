package org.sslabs.tvmaze.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.sslabs.tvmaze.data.api.TvMazeApi
import org.sslabs.tvmaze.data.api.mapper.ShowApiMapper
import org.sslabs.tvmaze.data.local.db.dao.ShowDao
import org.sslabs.tvmaze.data.local.db.mapper.ShowCacheMapper
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
}