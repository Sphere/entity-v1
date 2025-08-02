package com.sunbird.entity.repository.jpa;

import com.sunbird.entity.model.dao.Entity;
import com.sunbird.entity.model.dao.EntityDao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EntitiesRepository extends CrudRepository<Entity, Integer>, CustomRepository<Entity> {

    List<Entity> findByType(String type);
}
