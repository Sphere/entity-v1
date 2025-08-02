package com.sunbird.entity.repository.jpa;

import org.springframework.data.repository.CrudRepository;

import com.sunbird.entity.model.dao.EntityDao;

import java.util.List;

public interface EntityRepository extends CrudRepository<EntityDao, Integer>, CustomRepository<EntityDao> {

    List<EntityDao> findByType(String type);
}
