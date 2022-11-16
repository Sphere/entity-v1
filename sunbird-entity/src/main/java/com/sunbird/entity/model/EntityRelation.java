package com.sunbird.entity.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EntityRelation {

	private Integer id;
	private String parent;
	private int parentId;
	private String child;
	private List<Integer> childIds;
	private String status;

}
