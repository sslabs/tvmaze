package org.sslabs.tvmaze.data.api.mapper

import org.sslabs.tvmaze.data.api.entity.EpisodeApiEntity
import org.sslabs.tvmaze.data.model.Episode
import org.sslabs.tvmaze.util.EntityMapper
import java.lang.IllegalStateException
import javax.inject.Inject

class EpisodeApiMapper @Inject constructor() : EntityMapper<EpisodeApiEntity, Episode> {

    private var showId: Int = -1

    override fun fromEntity(entity: EpisodeApiEntity) = let {
        if (showId < 0) throw IllegalStateException("Must call forShow before")
        Episode(
            id = entity.id,
            showId = showId,
            name = entity.name,
            number = entity.number,
            season = entity.season,
            summary = entity.summary,
            image = entity.image?.original
        )
    }

    override fun toEntity(domainModel: Episode): EpisodeApiEntity {
        throw NotImplementedError()
    }

    fun forShow(showId: Int): EpisodeApiMapper = apply {
        if (showId < 0) throw IllegalArgumentException("showId is invalid: $showId")
        this.showId = showId
    }
}
