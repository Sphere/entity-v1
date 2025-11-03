package com.sunbird.entity.repository.elasticsearch;

import com.sunbird.entity.model.dao.Entity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface EntityESRepository extends ElasticsearchRepository<Entity, Integer> {
    List<Entity> findByType(String type);
    Optional<Entity> findByCode(String code);

}

