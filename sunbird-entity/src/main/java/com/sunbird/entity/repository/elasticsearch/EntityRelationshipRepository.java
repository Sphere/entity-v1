package com.sunbird.entity.repository.elasticsearch;


import com.sunbird.entity.model.EntityRelationship;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface EntityRelationshipRepository extends ElasticsearchRepository<EntityRelationship, String> {
}
