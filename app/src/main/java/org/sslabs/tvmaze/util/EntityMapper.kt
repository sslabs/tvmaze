package org.sslabs.tvmaze.util

interface EntityMapper<Entity, DomainModel> {
    fun fromEntity(entity: Entity): DomainModel
    fun toEntity(domainModel: DomainModel): Entity
    fun mapFromEntityList(entities: List<Entity>): List<DomainModel> =
        entities.map { fromEntity(it) }

    fun mapToEntityList(domainModels: List<DomainModel>): List<Entity> =
        domainModels.map { toEntity(it) }
}
