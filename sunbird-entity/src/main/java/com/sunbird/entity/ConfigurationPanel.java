package com.sunbird.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.sunbird.entity.model.dao.ChildEntityDao;
import com.sunbird.entity.model.dao.ParentEntityDao;
import com.sunbird.entity.repository.ParentEntityRepository;
import com.sunbird.entity.util.Constants;
import com.sunbird.entity.util.QueryUtils;

@Component
public class ConfigurationPanel implements ApplicationRunner {

	static ParentEntityRepository parentEntityRepo;

	@Autowired
	ConfigurationPanel(ParentEntityRepository parentEntityRepo) {
		this.parentEntityRepo = parentEntityRepo;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationPanel.class);

	private static ConcurrentHashMap<Integer, List<Integer>> entityRelation = new ConcurrentHashMap<>();

	@Override
	public void run(ApplicationArguments args) throws Exception {
		loadRelationId();
	}

	public static void loadRelationId() {
		try {
			String query = QueryUtils.Clauses.FROM + QueryUtils.Table.PARENT_NODE;
			List<ParentEntityDao> response = parentEntityRepo.customFindAll(query, new HashMap<>());
			response.forEach((responseObj) -> {
				List<Integer> childId = new ArrayList<>();
				if (entityRelation.containsKey(responseObj.getParentId())) {
					childId = entityRelation.get(responseObj.getParentId());
				}
				if (responseObj.getChildren() != null) {
					childId.addAll(responseObj.getChildren().stream().map(ChildEntityDao::getChildId)
							.collect(Collectors.toList()));
				}
				entityRelation.put(responseObj.getParentId(), childId);
			});
		} catch (Exception e) {
			LOGGER.error(String.format(Constants.Exception.EXCEPTION_METHOD, "", e.getMessage()));
		}
	}

	public static List<Integer> getChildId(Integer parentId) {
		if (entityRelation.containsKey(parentId)) {
			return entityRelation.get(parentId);
		}
		return new ArrayList<>();
	}

	public static void setChildId(Integer parentId, List<Integer> childId) {
		entityRelation.put(parentId, childId);
	}

}
