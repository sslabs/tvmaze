package org.sslabs.tvmaze.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.sslabs.tvmaze.data.local.db.TvMazeDatabase
import org.sslabs.tvmaze.data.local.db.dao.EpisodeDao
import org.sslabs.tvmaze.data.local.db.dao.ShowDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TvMazeDatabase =
        Room.databaseBuilder(
            context,
            TvMazeDatabase::class.java,
            TvMazeDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideShowDao(tvMazeDatabase: TvMazeDatabase): ShowDao =
        tvMazeDatabase.showDao()

    @Singleton
    @Provides
    fun provideEpisodeDao(tvMazeDatabase: TvMazeDatabase): EpisodeDao =
        tvMazeDatabase.episodeDao()
}
