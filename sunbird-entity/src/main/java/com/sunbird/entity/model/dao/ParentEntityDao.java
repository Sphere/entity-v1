package com.sunbird.entity.model.dao;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
@Entity(name = QueryUtils.Table.PARENT_NODE)
@Table(name = QueryUtils.Table.PARENT_NODE)
public class ParentEntityDao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column
	private String type;

	@Column(name = QueryUtils.TableFields.PARENT_ID)
	private int parentId;

	@Column
	private String child;

	@Column
	private String status;

	@JoinColumn(name = QueryUtils.TableFields.PARENT_MAP_ID)
	@OneToMany(fetch = FetchType.EAGER)
	private List<ChildEntityDao> children;

}
