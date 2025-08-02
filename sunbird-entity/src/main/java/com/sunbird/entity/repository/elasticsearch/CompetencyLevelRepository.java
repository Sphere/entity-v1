package com.sunbird.entity.repository.elasticsearch;


import com.sunbird.entity.model.CompetencyLevelMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CompetencyLevelRepository extends ElasticsearchRepository<CompetencyLevelMapping, String> {
}
