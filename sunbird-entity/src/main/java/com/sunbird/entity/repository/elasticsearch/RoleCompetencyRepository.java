package com.sunbird.entity.repository.elasticsearch;


import com.sunbird.entity.model.RoleCompetencyMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface RoleCompetencyRepository extends ElasticsearchRepository<RoleCompetencyMapping, String> {
    List<RoleCompetencyMapping> findByRoleId(Integer roleId);
}
