package com.sunbird.entity.repository;

import org.springframework.data.repository.CrudRepository;

import com.sunbird.entity.model.dao.EntityDao;

public interface EntityRepository extends CrudRepository<EntityDao, Integer>, CustomRepository<EntityDao> {

}
