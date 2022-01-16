package org.sslabs.tvmaze.data.local.db.mapper

import org.sslabs.tvmaze.data.local.db.entity.EpisodeCacheEntity
import org.sslabs.tvmaze.data.model.Episode
import org.sslabs.tvmaze.util.EntityMapper
import javax.inject.Inject

class EpisodeCacheMapper @Inject constructor() : EntityMapper<EpisodeCacheEntity, Episode> {

    override fun fromEntity(entity: EpisodeCacheEntity) = Episode(
        id = entity.id,
        showId = entity.showId,
        name = entity.name,
        number = entity.number,
        season = entity.season,
        summary = entity.summary,
        image = entity.image
    )

    override fun toEntity(domainModel: Episode) = EpisodeCacheEntity(
        id = domainModel.id,
        showId = domainModel.showId,
        name = domainModel.name,
        number = domainModel.number,
        season = domainModel.season,
        summary = domainModel.summary,
        image = domainModel.image
    )
}
