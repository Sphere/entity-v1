package com.sunbird.entity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "entity_relationships",type = "doc")
public class EntityRelationship {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;


	private String type; // e.g. POSITION_ROLE, ROLE_ACTIVITY, ACTIVITY_COMPETENCY_LEVEL

	private String parentId;
	private List<String> childIds;
	private Map<String, List<String>> childMap;

}

