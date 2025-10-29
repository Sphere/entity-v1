package com.sunbird.entity.repository.elasticsearch;

import com.sunbird.entity.model.dao.Entity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EntityESRepository extends ElasticsearchRepository<Entity, Integer> {
    List<Entity> findByType(String type);

}

