package com.sunbird.entity.mapper.es;

import com.sunbird.entity.model.dao.Entity;
import com.sunbird.entity.model.es.EntityDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityMapper {

    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

//    @Mapping(target = "children", ignore = true)
    EntityDocument toDocument(Entity entity);
}

