package com.sunbird.entity.service;

import com.sunbird.entity.model.DTO.EntityResponseDTO;
import com.sunbird.entity.model.DTO.EntitySearchRequestDTO;
import com.sunbird.entity.model.es.EntityDocument;

import java.util.List;

//TODO: Implementation class and interface are not proper few unfinished method in implementation class
public interface EntitySearchService {
    public List<EntityDocument> searchByName(String searchText);

    public List<EntityDocument> searchWithFilters(
            String searchText,
            String type,
            String status,
            String level);


    public List<EntityResponseDTO> findEntityWithGenericAttributeValue(EntitySearchRequestDTO entitySearchRequestDTO);
}
