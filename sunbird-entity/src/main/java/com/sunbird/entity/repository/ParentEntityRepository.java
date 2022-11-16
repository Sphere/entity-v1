package com.sunbird.entity.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sunbird.entity.model.dao.ParentEntityDao;
import com.sunbird.entity.util.QueryUtils;

@Repository
public interface ParentEntityRepository
		extends CrudRepository<ParentEntityDao, Long>, CustomRepository<ParentEntityDao> {

	@Query("SELECT id FROM " + QueryUtils.Table.PARENT_NODE + " WHERE parent_id = ?1 AND child = ?2")
	Integer getRelationId(int parent_id, String child);

}
