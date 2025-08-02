package com.sunbird.entity.repository.elasticsearch;

import com.sunbird.entity.model.PositionRoleMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PositionRoleRepository extends ElasticsearchRepository<PositionRoleMapping, String> {
    List<PositionRoleMapping> findByPositionId(Integer positionId);

}
