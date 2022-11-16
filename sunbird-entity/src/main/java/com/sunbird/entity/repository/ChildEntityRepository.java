package com.sunbird.entity.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sunbird.entity.model.dao.ChildEntityDao;
import com.sunbird.entity.util.QueryUtils;

@Repository
public interface ChildEntityRepository extends CrudRepository<ChildEntityDao, Long> {

	@Transactional
	@Modifying
	@Query("DELETE FROM " + QueryUtils.Table.CHILD_NODE + " WHERE parent_map_id = ?1")
	void deleteByMapId(Integer mapId);

}
