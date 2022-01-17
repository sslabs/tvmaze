package org.sslabs.tvmaze.data.api.mapper

import org.sslabs.tvmaze.data.api.entity.ShowApiEntity
import org.sslabs.tvmaze.data.model.Show
import org.sslabs.tvmaze.util.EntityMapper
import javax.inject.Inject

class ShowApiMapper @Inject constructor() : EntityMapper<ShowApiEntity, Show> {

    override fun fromEntity(entity: ShowApiEntity): Show {
        return Show(
            id = entity.id,
            name = entity.name,
            image = entity.image?.original,
            time = entity.schedule?.time,
            days = entity.schedule?.days,
            genres = entity.genres,
            summary = entity.summary,
            favorite = false
        )
    }

    override fun toEntity(domainModel: Show): ShowApiEntity {
        throw NotImplementedError()
    }
}
