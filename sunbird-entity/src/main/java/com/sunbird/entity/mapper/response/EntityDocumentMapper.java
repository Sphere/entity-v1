package com.sunbird.entity.mapper.response;

import com.sunbird.entity.mapper.es.EntityMapper;
import com.sunbird.entity.model.DTO.EntityResponseDTO;
import com.sunbird.entity.model.es.EntityDocument;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityDocumentMapper {

    EntityDocumentMapper INSTANCE = Mappers.getMapper(EntityDocumentMapper.class);

    EntityResponseDTO toEntityResponse(EntityDocument entityDocument);
}
