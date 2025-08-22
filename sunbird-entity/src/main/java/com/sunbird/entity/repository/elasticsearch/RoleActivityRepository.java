package com.sunbird.entity.repository.elasticsearch;


import com.sunbird.entity.model.RoleActivityMapping;
import com.sunbird.entity.model.RoleActivityMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface RoleActivityRepository extends ElasticsearchRepository<RoleActivityMapping, String> {
    List<RoleActivityMapping> findByRoleId(Integer roleId);
}
