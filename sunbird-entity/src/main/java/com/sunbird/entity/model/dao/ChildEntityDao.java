package com.sunbird.entity.model.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sunbird.entity.util.QueryUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = QueryUtils.Table.CHILD_NODE)
@Table(name = QueryUtils.Table.CHILD_NODE)
public class ChildEntityDao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = QueryUtils.TableFields.PARENT_MAP_ID)
	private int parentMapId;

	@Column(name = QueryUtils.TableFields.CHILD_ID)
	private int childId;
}
