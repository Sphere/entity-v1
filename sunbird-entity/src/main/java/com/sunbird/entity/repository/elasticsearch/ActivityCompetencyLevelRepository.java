package com.sunbird.entity.repository.elasticsearch;


import com.sunbird.entity.model.ActivityCompetencyLevelMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ActivityCompetencyLevelRepository extends ElasticsearchRepository<ActivityCompetencyLevelMapping, String> {
    List<ActivityCompetencyLevelMapping> findByCompetencyId(Integer compId);
}
