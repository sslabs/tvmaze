package org.sslabs.tvmaze.data.local.db.mapper

import org.sslabs.tvmaze.data.local.db.entity.ShowCacheEntity
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.util.EntityMapper
import javax.inject.Inject

class ShowCacheMapper @Inject constructor() : EntityMapper<ShowCacheEntity, Show> {

    override fun fromEntity(entity: ShowCacheEntity) = Show(
        id = entity.id,
        name = entity.name,
        image = entity.image,
        time = entity.time,
        days = entity.days?.split(",")?.toList(),
        genres = entity.genres?.split(",")?.toList(),
        summary = entity.summary,
        favorite = entity.favorite
    )

    override fun toEntity(domainModel: Show) = ShowCacheEntity(
        id = domainModel.id,
        name = domainModel.name,
        image = domainModel.image,
        time = domainModel.time,
        days = domainModel.days?.joinToString(separator = ","),
        genres = domainModel.genres?.joinToString(separator = ","),
        summary = domainModel.summary,
        favorite = domainModel.favorite
    )
}
