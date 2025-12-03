package com.sunbird.entity.repository.elasticsearch;

import com.sunbird.entity.model.dao.Entity;
import com.sunbird.entity.model.es.EntityDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface EntityDocumentESRepository extends ElasticsearchRepository<EntityDocument, Integer> {
    Optional<Entity> findByCode(String code);

    List<EntityDocument> findByType(String type);

    List<EntityDocument> findByStatus(String status);

    List<EntityDocument> findByNameContaining(String name);

    List<EntityDocument> findByTypeAndStatus(String type, String status);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"name\": \"?0\"}}]}}")
    List<EntityDocument> customSearch(String searchText);

}

